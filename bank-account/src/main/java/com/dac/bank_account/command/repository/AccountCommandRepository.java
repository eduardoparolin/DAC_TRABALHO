package com.dac.bank_account.command.repository;

import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AccountCommandRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByClientId(Long ClientId);

    //Atualiza todos os contas que possuem o managerId antigo para o novo managerId, saga de deletar gerente
    //Retorna a quantidade de contas atualizadas
    @Modifying
    @Query("UPDATE Account a SET a.managerId = :newManagerId WHERE a.managerId = :oldManagerId")
    int updateManagerForAccounts(Long oldManagerId, Long newManagerId);

    //Encontra o managerId que possui mais contas com saldo positivo associadas
    //Em caso de empate, retorna o que possui menor soma de saldo positivo
    //Usado na saga de novo gerente para encontrar o gerente que vai doar uma conta
    @Query(
            value = """
        with d as (with b as (SELECT managerId, count(*) as c
                              FROM account
                              WHERE status = 'ATIVA'
                              GROUP BY managerId
                              ORDER BY COUNT(*) DESC)
                   select b.managerid
                   from b
                   where b.c = (select max(b.c) from b))
        select managerid
        from account
        where managerid in (select * from d)
          and balance >= 0
        group by managerid
        order by sum(balance) asc
        limit 1
    """,
            nativeQuery = true
    )
    Long findManagerWithMostAccountsAndLowestBalance();

    //Encontra a conta com menor saldo positivo de um gerente específico
    //Usado para selecionar qual conta será transferida para o novo gerente
    @Query(
            value = """
        SELECT *
        FROM account
        WHERE managerId = :managerId AND balance >= 0 AND status = 'ATIVA'
        ORDER BY balance ASC
        LIMIT 1
    """,
            nativeQuery = true
    )
    Optional<Account> findAccountWithLowestPositiveBalance(Long managerId);

    //Retorna uma lista de arrays contendo o managerId e a contagem de contas associadas a ele, saga de novo gerente
    @Query("SELECT a.managerId, COUNT(a) FROM Account a GROUP BY a.managerId")
    List<Object[]> findManagerAccountCountsRaw();


    //Encontra o managerId que possui menos contas associadas, saga de novo gerente, saga de autocadastro
    @Query(
            value = """
            SELECT managerId
            FROM account
            GROUP BY managerId
            ORDER BY COUNT(*) ASC
            LIMIT 1
            """,
            nativeQuery = true
    )
    Long findManagerWithLeastAccounts();

    //Encontra os managerIds que possuem a menor quantidade de contas associadas (para tie-breaking aleatório)
    //Retorna todos os managers que estão empatados com a menor quantidade de contas
    @Query(
            value = """
            SELECT managerId
            FROM account
            GROUP BY managerId
            HAVING COUNT(*) = (
                SELECT MIN(account_count)
                FROM (
                    SELECT COUNT(*) as account_count
                    FROM account
                    GROUP BY managerId
                ) as counts
            )
            """,
            nativeQuery = true
    )
    List<Long> findManagersWithLeastAccounts();

    //Encontra a primeira conta com status ATIVA associada ao gerente que vai doar uma conta para o novo gerente, saga de novo gerente
    Optional<Account> findFirstByManagerIdAndStatus(Long managerId, AccountStatus status);
}


