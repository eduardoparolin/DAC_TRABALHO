package com.example.orchestrator.service;

import com.example.orchestrator.dto.*;
import com.example.orchestrator.model.Saga;
import com.example.orchestrator.model.SagaStep;
import com.example.orchestrator.producer.SagaProducer;
import com.example.orchestrator.repository.SagaRepository;
import com.example.orchestrator.repository.SagaStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SagaOrchestratorService {

  private static final Logger log = LoggerFactory.getLogger(SagaOrchestratorService.class);

  private final SagaRepository sagaRepository;
  private final SagaStepRepository sagaStepRepository;
  private final SagaProducer sagaProducer;

  // Contexto para armazenar dados entre steps da saga
  private final Map<String, Map<String, Object>> sagaContexts = new ConcurrentHashMap<>();

  public SagaOrchestratorService(SagaRepository sagaRepository,
      SagaStepRepository sagaStepRepository,
      SagaProducer sagaProducer) {
    this.sagaRepository = sagaRepository;
    this.sagaStepRepository = sagaStepRepository;
    this.sagaProducer = sagaProducer;
  }

  @Transactional
  public String startCreateClientSaga(SagaRequest request) {
    String sagaId = UUID.randomUUID().toString();
    log.info("Starting CREATE_CLIENT saga with id: {}", sagaId);

    // Create saga
    Saga saga = new Saga(sagaId, "PENDING", Instant.now());
    sagaRepository.save(saga);

    // Initialize saga context with all data from request
    Map<String, Object> context = new HashMap<>();
    context.put("sagaId", sagaId);
    Map<String, Object> data = request.getData();
    if (data != null) {
      context.put("cpf", data.get("cpf"));
      context.put("name", data.get("name"));
      context.put("email", data.get("email"));
      context.put("phone", data.get("phone"));
      context.put("salary", data.get("salary"));
      context.put("street", data.get("street"));
      context.put("complement", data.get("complement"));
      context.put("zipCode", data.get("zipCode"));
      context.put("city", data.get("city"));
      context.put("state", data.get("state"));
    }
    sagaContexts.put(sagaId, context);

    // Create saga step
    SagaStep step = new SagaStep("CREATE_CLIENT", "PENDING",
        convertMapToString(request.getData()), "DELETE_CLIENT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    // Send message to client service
    ClientMessageRequest clientRequest = new ClientMessageRequest();
    clientRequest.setSagaId(sagaId);
    clientRequest.setAction("CREATE_CLIENT");
    clientRequest.setCpf((String) context.get("cpf"));
    clientRequest.setName((String) context.get("name"));
    clientRequest.setEmail((String) context.get("email"));
    clientRequest.setPhone((String) context.get("phone"));

    Object salaryObj = context.get("salary");
    if (salaryObj != null) {
      if (salaryObj instanceof Number) {
        clientRequest.setSalary(((Number) salaryObj).doubleValue());
      }
    }

    clientRequest.setStreet((String) context.get("street"));
    clientRequest.setComplement((String) context.get("complement"));
    clientRequest.setZipCode((String) context.get("zipCode"));
    clientRequest.setCity((String) context.get("city"));
    clientRequest.setState((String) context.get("state"));

    sagaProducer.sendToClientService(clientRequest);

    return sagaId;
  }

  public String startUpdateClientSaga(SagaRequest request) {
    String sagaId = UUID.randomUUID().toString();
    log.info("Starting UPDATE_CLIENT saga with id: {}", sagaId);

    // Create saga
    Saga saga = new Saga(sagaId, "PENDING", Instant.now());
    sagaRepository.save(saga);

    // Initialize saga context with all data from request
    Map<String, Object> context = new HashMap<>();
    context.put("sagaId", sagaId);
    Map<String, Object> data = request.getData();

    if (data != null) {
      context.put("cpf", data.get("cpf"));
      context.put("name", data.get("name"));
      context.put("email", data.get("email"));
      context.put("salary", data.get("salary"));
      context.put("clientId", data.get("clientId"));
    }

    sagaContexts.put(sagaId, context);

    // Create saga step
    SagaStep step = new SagaStep("UPDATE_CLIENT", "PENDING",
        convertMapToString(request.getData()), "DELETE_CLIENT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    // Send message to client service
    ClientMessageRequest clientRequest = new ClientMessageRequest();
    clientRequest.setSagaId(sagaId);
    clientRequest.setAction("UPDATE_CLIENT");
    clientRequest.setClientId(Long.valueOf((Integer) context.get("clientId")));
    clientRequest.setCpf((String) context.get("cpf"));
    clientRequest.setName((String) context.get("name"));
    clientRequest.setEmail((String) context.get("email"));

    Object salaryObj = context.get("salary");
    if (salaryObj != null) {
      if (salaryObj instanceof Number) {
        clientRequest.setSalary(((Number) salaryObj).doubleValue());
      }
    }

    sagaProducer.sendToClientService(clientRequest);

    return sagaId;
  }

  public String startDeleteManagerSaga(SagaRequest request) {
    String sagaId = UUID.randomUUID().toString();
    log.info("Starting DELETE_MANAGER saga with id: {}", sagaId);

    // Create saga
    Saga saga = new Saga(sagaId, "PENDING", Instant.now());
    sagaRepository.save(saga);

    // Initialize saga context with all data from request
    Map<String, Object> context = new HashMap<>();
    context.put("sagaId", sagaId);
    Map<String, Object> data = request.getData();

    if (data != null) {
        context.put("managerId", data.get("managerId"));
        context.put("cpf", data.get("cpf"));
        context.put("requestedById", data.get("requestedById"));
        context.put("role", data.get("role"));
    }

    sagaContexts.put(sagaId, context);

    // Create saga step
    SagaStep step = new SagaStep("DELETE_MANAGER_REGISTER", "PENDING",
            convertMapToString(request.getData()), "REVERT_DELETE_MANAGER");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    Map<String, Object> managerRequest = new HashMap<>();
    managerRequest.put("sagaId", sagaId);
    managerRequest.put("action", "DELETE_MANAGER_MS");
    managerRequest.put("cpf", context.get("cpf"));
    managerRequest.put("managerId", context.get("managerId"));
    managerRequest.put("requestedById", context.get("requestedById"));

    sagaProducer.sendToManagerService(managerRequest);
    return sagaId;
  }

  @Transactional
  public String startCreateManagerSaga(SagaRequest request) {
    String sagaId = UUID.randomUUID().toString();
    log.info("Starting CREATE_MANAGER saga with id: {}", sagaId);

    // Create saga
    Saga saga = new Saga(sagaId, "PENDING", Instant.now());
    sagaRepository.save(saga);

    // Initialize saga context with all data from request
    Map<String, Object> context = new HashMap<>();
    context.put("sagaId", sagaId);
    Map<String, Object> data = request.getData();

    if (data != null) {
      context.put("cpf", data.get("cpf"));
      context.put("name", data.get("name"));
      context.put("email", data.get("email"));
      context.put("password", data.get("password"));
      context.put("type", data.get("type"));
    }
    sagaContexts.put(sagaId, context);

    // Start with CREATE_MANAGER step
    createManagerStep(sagaId);

    return sagaId;
  }

  @Transactional
  public String startApproveClientSaga(SagaRequest request) {
    String sagaId = UUID.randomUUID().toString();
    log.info("Starting APPROVE_CLIENT saga with id: {}", sagaId);

    // Create saga
    Saga saga = new Saga(sagaId, "PENDING", Instant.now());
    sagaRepository.save(saga);

    // Initialize saga context with data from request
    Map<String, Object> context = new HashMap<>();
    context.put("sagaId", sagaId);
    Map<String, Object> data = request.getData();

    if (data != null) {
      context.put("cpf", data.get("cpf"));
      context.put("clientId", data.get("clientId"));
      context.put("managerId", data.get("managerId"));
      context.put("accountId", data.get("accountId"));
    }
    sagaContexts.put(sagaId, context);

    // Start with APPROVE_CLIENT step
    approveClientStep(sagaId);

    return sagaId;
  }

  @Transactional
  public void processResult(SagaResult result) {
    log.info("Processing result from {}: status={}, action={}, sagaId={}, result={}",
        result.getSource(), result.getStatus(), result.getAction(), result.getSagaId(), result);

    // Encontrar sagaId através do resultado (assumindo que vem no resultado)
    // Se não vier, precisamos buscar de outra forma
    String sagaId = result.getSagaId();
    if (sagaId == null) {
      log.error("SagaId not found in result");
      return;
    }

    if ("SUCCESS".equals(result.getStatus())) {
      log.info("SUCCESS result received, storing in context and advancing to next step");
      storeInContext(sagaId, result);
      advanceToNextStep(sagaId, result.getAction());
    } else {
      log.error("FAILURE result received, executing compensation");
      executeCompensation(sagaId, result);
    }
  }

  private void storeInContext(String sagaId, SagaResult result) {
    Map<String, Object> context = sagaContexts.get(sagaId);
    if (context == null) {
      context = new HashMap<>();
      sagaContexts.put(sagaId, context);
    }

    if (result.getClientId() != null) {
      context.put("clientId", result.getClientId());
    }
    if (result.getManagerId() != null) {
      context.put("managerId", result.getManagerId());
    }
    if (result.getAccountId() != null) {
      context.put("accountId", result.getAccountId());
    }
    if (result.getAccountNumber() != null) {
      context.put("accountNumber", result.getAccountNumber());
    }
    if (result.getGeneratedPassword() != null) {
      context.put("generatedPassword", result.getGeneratedPassword());
    }
    if(result.getManagerIdLessAccounts() != null) {
      context.put("managerIdLessAccounts", result.getManagerIdLessAccounts());
    }
  }

  private void advanceToNextStep(String sagaId, String completedAction) {
    log.info("Advancing saga {} after completing action: '{}'", sagaId, completedAction);

    switch (completedAction) {
      case "CREATE_MANAGER":
      case "CREATE_MANAGER_RESULT":
        log.info("Matched CREATE_MANAGER, proceeding to createManagerAuthStep");
        createManagerAuthStep(sagaId);
        break;
      case "CREATE_MANAGER_AUTH":
      case "CREATE_MANAGER_AUTH_RESULT":
        log.info("Matched CREATE_MANAGER_AUTH, proceeding to assignAccountToNewManagerStep");
        assignAccountToNewManagerStep(sagaId);
        break;
      case "ASSIGN_ACCOUNT_TO_NEW_MANAGER":
      case "ASSIGN_ACCOUNT_TO_NEW_MANAGER_RESULT":
        log.info("Matched ASSIGN_ACCOUNT_TO_NEW_MANAGER, completing CREATE_MANAGER saga");
        completeManagerCreationSaga(sagaId);
        break;
      case "DELETE":
      case "DELETE_RESULT":
        log.info("Matched DELETE, completing saga");
        completeSaga(sagaId, "DELETE MANAGER");
        break;
      case "DELETE_MANAGER":
      case "DELETE_MANAGER_RESULT":
        log.info("Matched DELETE_MANAGER, proceeding to deleteManagerAuth");
        deleteManagerAuth(sagaId);
        break;
      case "DELETE_MANAGER_MS":
      case "DELETE_MANAGER_MS_RESULT":
        log.info("Matched DELETE_MANAGER_MS, proceeding to assignNewManagerToAccounts");
        assignNewManagerToAccounts(sagaId);
        break;
      case "UPDATE":
      case "UPDATE_RESULT":
        log.info("Matched UPDATE_CLIENT, proceeding to updateSalaryAccount");
        updateSalaryAccount(sagaId);
      case "UPDATE_CLIENT":
      case "UPDATE_CLIENT_RESULT":
        log.info("Matched UPDATE_CLIENT, proceeding to updateAuth");
        updateAuth(sagaId);
        break;
      case "CREATE_CLIENT":
      case "CREATE_CLIENT_RESULT":
        log.info("Matched CREATE_CLIENT, proceeding to assignManagerStep");
        assignManagerStep(sagaId);
        break;
      case "ASSIGN_MANAGER":
      case "ASSIGN_MANAGER_RESULT":
        log.info("Matched ASSIGN_MANAGER, proceeding to updateClientManagerStep");
        updateClientManagerStep(sagaId);
        break;
      case "UPDATE_CLIENT_MANAGER":
      case "UPDATE_CLIENT_MANAGER_RESULT":
        log.info("Matched UPDATE_CLIENT_MANAGER, proceeding to createAccountStep");
        createAccountStep(sagaId);
        break;
      case "CREATE_ACCOUNT":
      case "CREATE_ACCOUNT_RESULT":
        log.info("Matched CREATE_ACCOUNT, proceeding to linkAccountStep");
        linkAccountStep(sagaId);
        break;
      case "LINK_ACCOUNT":
      case "LINK_ACCOUNT_RESULT":
        log.info("Matched LINK_ACCOUNT, proceeding to createUserStep (create auth with temp password)");
        createUserStep(sagaId);
        break;
      case "APPROVE_CLIENT":
      case "APPROVE_CLIENT_RESULT":
        log.info("Matched APPROVE_CLIENT, proceeding to incrementAccountCountStep");
        incrementAccountCountStep(sagaId);
        break;
      case "INCREMENT_ACCOUNT_COUNT":
      case "INCREMENT_ACCOUNT_COUNT_RESULT":
        log.info("Matched INCREMENT_ACCOUNT_COUNT, proceeding to updateAccountStatusStep");
        updateAccountStatusStep(sagaId);
        break;
      case "UPDATE_ACCOUNT_STATUS":
      case "UPDATE_ACCOUNT_STATUS_RESULT":
        log.info("Matched UPDATE_ACCOUNT_STATUS, proceeding to updatePasswordStep");
        updatePasswordStep(sagaId);
        break;
      case "CREATE":
      case "CREATE_RESULT":
        log.info("Matched CREATE (user created with temp password), completing AUTO_CADASTRO saga");
        completeSaga(sagaId, "AGUARDANDO_APROVACAO");
        break;
      case "UPDATE_PASSWORD":
      case "UPDATE_PASSWORD_RESULT":
        log.info("Matched UPDATE_PASSWORD, completing APPROVE_CLIENT saga");
        completeSaga(sagaId, "CADASTRO_COMPLETO");
        break;
      default:
        log.warn("Unknown action to advance: '{}' (length: {})", completedAction, completedAction.length());
    }
  }

  private void deleteManagerAuth(String sagaId) {
    log.info("Step 3: Delete manager auth {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("DELETE_MANAGER_AUTH", "PENDING",
        "Deleting manager auth", "REVERT_DELETE_MANAGER_AUTH");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    AuthPayload payload = new AuthPayload();
    payload.setAction("DELETE");
    payload.setMessageSource("orchestrator");
    payload.setSagaId(sagaId);

    AuthPayloadData data = new AuthPayloadData();
    data.setId(Long.parseLong(String.valueOf(context.get("managerId"))));
    data.setRole(context.get("role").toString());
    payload.setRequestedById(Long.parseLong(String.valueOf(context.get("requestedById"))));
    payload.setData(data);

    sagaProducer.sendToAuthService(payload);
  }

  private void updateSalaryAccount(String sagaId) {
    log.info("Step 2: Update salary in account {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("UPDATE_SALARY", "PENDING",
        "updating auth email", "REVERT_UPDATE_SALARY");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    Map<String, Object> updateSalaryRequest = new HashMap<>();
    updateSalaryRequest.put("sagaId", sagaId);
    updateSalaryRequest.put("action", "UPDATE_LIMIT");
    updateSalaryRequest.put("idUser", context.get("clientId"));
    updateSalaryRequest.put("salary", context.get("salary"));

    sagaProducer.sendToAccountService(updateSalaryRequest);
  }

  private void updateAuth(String sagaId) {
    log.info("Step 2: Update auth for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("UPDATE_AUTH", "PENDING",
        "updating auth", "REVERT_UPDATE_EMAIL");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    AuthPayload payload = new AuthPayload();
    payload.setAction("UPDATE");
    payload.setMessageSource("orchestrator");
    payload.setSagaId(sagaId);

    AuthPayloadData data = new AuthPayloadData();
    data.setId(Long.parseLong(String.valueOf(context.get("clientId"))));
    data.setName(context.get("name").toString());
    data.setCpf(context.get("cpf").toString());
    data.setEmail((String) context.get("email"));
    data.setRole((String) context.get("role"));

    payload.setData(data);

    sagaProducer.sendToAuthService(payload);
  }

  private void assignNewManagerToAccounts(String sagaId) {
    log.info("Step 2: Assigning new manager to accounts");
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("DELETE_MANAGER_ACCOUNT", "PENDING",
        "Deleting manager", "REVERT_DELETE_MANAGER_ACCOUNT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    Map<String, Object> updateManagersToAccount = new HashMap<>();
    updateManagersToAccount.put("sagaId", sagaId);
    updateManagersToAccount.put("action", "DELETE_MANAGER");
    updateManagersToAccount.put("managerId", context.get("managerId"));
    updateManagersToAccount.put("oldManagerId", context.get("managerId"));
    updateManagersToAccount.put("newManagerId", context.get("managerIdLessAccounts"));

    sagaProducer.sendToAccountService(updateManagersToAccount);
  }

  private void assignManagerStep(String sagaId) {
    log.info("Step 2: Assigning manager for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("ASSIGN_MANAGER", "PENDING",
        "Assigning manager", "UNASSIGN_MANAGER");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    // Send message to manager service to assign a manager
    Map<String, Object> managerRequest = new HashMap<>();
    managerRequest.put("sagaId", sagaId);
    managerRequest.put("action", "ASSIGN_MANAGER");
    managerRequest.put("clientId", context.get("clientId"));

    sagaProducer.sendToManagerService(managerRequest);
  }

  private void updateClientManagerStep(String sagaId) {
    log.info("Step 2.5: Updating client with managerId for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("UPDATE_CLIENT_MANAGER", "PENDING",
        "Updating client manager", "REVERT_UPDATE_CLIENT_MANAGER");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    ClientMessageRequest request = new ClientMessageRequest();
    request.setSagaId(sagaId);
    request.setAction("UPDATE_CLIENT_MANAGER");
    request.setClientId((Long) context.get("clientId"));
    request.setManagerId((Long) context.get("managerId"));

    sagaProducer.sendToClientService(request);
  }

  private void createAccountStep(String sagaId) {
    log.info("Step 3: Creating account for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("CREATE_ACCOUNT", "PENDING",
        "Creating account", "REJECT_ACCOUNT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    AccountSagaEvent event = new AccountSagaEvent();
    event.setSagaId(sagaId);
    event.setAction("CREATE_ACCOUNT");
    event.setClientId((Long) context.get("clientId"));
    event.setManagerId((Long) context.get("managerId")); // Manager ID from ASSIGN_MANAGER step

    Object salaryObj = context.get("salary");
    if (salaryObj != null && salaryObj instanceof Number) {
      event.setSalary(((Number) salaryObj).doubleValue());
    }

    sagaProducer.sendToAccountService(event);
  }

  private void linkAccountStep(String sagaId) {
    log.info("Step 4: Linking account to client for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);
    log.info("Context for saga {}: {}", sagaId, context);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("LINK_ACCOUNT", "PENDING",
        "Linking account to client", "UNLINK_ACCOUNT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    ClientMessageRequest request = new ClientMessageRequest();
    request.setSagaId(sagaId);
    request.setAction("LINK_ACCOUNT");
    request.setClientId((Long) context.get("clientId"));

    Object accountNumberObj = context.get("accountNumber");
    log.info("Account number from context: {} (type: {})", accountNumberObj, accountNumberObj != null ? accountNumberObj.getClass().getName() : "null");
    if (accountNumberObj != null) {
      if (accountNumberObj instanceof String) {
        request.setAccountNumber((String) accountNumberObj);
      } else if (accountNumberObj instanceof Number) {
        request.setAccountNumber(String.valueOf(accountNumberObj));
      }
    }
    log.info("Sending LINK_ACCOUNT request with accountNumber: {}", request.getAccountNumber());

    sagaProducer.sendToClientService(request);
  }

  private void approveClientStep(String sagaId) {
    log.info("Step 1 (Approval): Approving client for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("APPROVE_CLIENT", "PENDING",
        "Approving client", "REJECT_CLIENT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    ClientMessageRequest request = new ClientMessageRequest();
    request.setSagaId(sagaId);
    request.setAction("APPROVE_CLIENT");

    // Handle type conversion for clientId
    Object clientIdObj = context.get("clientId");
    if (clientIdObj instanceof Integer) {
      request.setClientId(((Integer) clientIdObj).longValue());
    } else if (clientIdObj instanceof Long) {
      request.setClientId((Long) clientIdObj);
    }

    // Handle type conversion for managerId
    Object managerIdObj = context.get("managerId");
    if (managerIdObj instanceof Integer) {
      request.setManagerId(((Integer) managerIdObj).longValue());
    } else if (managerIdObj instanceof Long) {
      request.setManagerId((Long) managerIdObj);
    }

    sagaProducer.sendToClientService(request);
  }

  private void incrementAccountCountStep(String sagaId) {
    log.info("Step 4.5: Incrementing manager account count for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("INCREMENT_ACCOUNT_COUNT", "PENDING",
        "Incrementing manager account count", "DECREMENT_ACCOUNT_COUNT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    Map<String, Object> managerRequest = new HashMap<>();
    managerRequest.put("sagaId", sagaId);
    managerRequest.put("action", "INCREMENT_ACCOUNT_COUNT");
    managerRequest.put("managerId", context.get("managerId"));

    sagaProducer.sendToManagerService(managerRequest);
  }

  private void updateAccountStatusStep(String sagaId) {
    log.info("Step 5: Updating account status for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("UPDATE_ACCOUNT_STATUS", "PENDING",
        "Activating account", "REJECT_ACCOUNT_STATUS");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    AccountSagaEvent event = new AccountSagaEvent();
    event.setSagaId(sagaId);
    event.setAction("UPDATE_ACCOUNT_STATUS");

    // Handle type conversion for clientId
    Object clientIdObj = context.get("clientId");
    if (clientIdObj instanceof Integer) {
      event.setClientId(((Integer) clientIdObj).longValue());
    } else if (clientIdObj instanceof Long) {
      event.setClientId((Long) clientIdObj);
    }

    event.setIsApproved(true);

    sagaProducer.sendToAccountService(event);
  }

  private void createUserStep(String sagaId) {
    log.info("Step 6: Creating user with temporary password for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    if (context == null) {
      log.error("Saga context not found for sagaId: {}. Skipping createUserStep.", sagaId);
      return;
    }

    Saga saga = sagaRepository.findById(sagaId).orElse(null);
    if (saga == null) {
      log.error("Saga not found in database for sagaId: {}. Skipping createUserStep.", sagaId);
      sagaContexts.remove(sagaId);
      return;
    }

    SagaStep step = new SagaStep("CREATE_USER", "PENDING",
        "Creating user with temporary password", "DELETE_USER");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    AuthPayload payload = new AuthPayload();
    payload.setAction("CREATE");
    payload.setMessageSource("orchestrator");
    payload.setSagaId(sagaId);

    AuthPayloadData data = new AuthPayloadData();
    data.setId(Long.parseLong(String.valueOf(context.get("clientId"))));
    data.setName(context.get("name").toString());
    data.setCpf(context.get("cpf").toString());
    data.setEmail((String) context.get("email"));
    data.setRole("CLIENTE");

    payload.setData(data);

    sagaProducer.sendToAuthService(payload);
  }

  private void updatePasswordStep(String sagaId) {
    log.info("Step: Updating password and sending email for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    if (context == null) {
      log.error("Saga context not found for sagaId: {}. Skipping updatePasswordStep.", sagaId);
      return;
    }

    Saga saga = sagaRepository.findById(sagaId).orElse(null);
    if (saga == null) {
      log.error("Saga not found in database for sagaId: {}. Skipping updatePasswordStep.", sagaId);
      sagaContexts.remove(sagaId);
      return;
    }

    SagaStep step = new SagaStep("UPDATE_PASSWORD", "PENDING",
        "Updating user password and sending email", "REVERT_PASSWORD");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    AuthPayload payload = new AuthPayload();
    payload.setAction("UPDATE_PASSWORD");
    payload.setMessageSource("orchestrator");
    payload.setSagaId(sagaId);

    AuthPayloadData data = new AuthPayloadData();
    data.setId(Long.parseLong(String.valueOf(context.get("clientId"))));
    data.setCpf(context.get("cpf").toString());
    data.setRole("CLIENTE");

    payload.setData(data);

    sagaProducer.sendToAuthService(payload);
  }

  private void createManagerStep(String sagaId) {
    log.info("Step 1: Creating manager for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("CREATE_MANAGER", "PENDING",
        "Creating manager", "DELETE_MANAGER_COMPENSATION");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    Map<String, Object> managerRequest = new HashMap<>();
    managerRequest.put("sagaId", sagaId);
    managerRequest.put("action", "CREATE_MANAGER");
    managerRequest.put("cpf", context.get("cpf"));
    managerRequest.put("name", context.get("name"));
    managerRequest.put("email", context.get("email"));
    managerRequest.put("password", context.get("password"));
    managerRequest.put("type", context.get("type"));

    sagaProducer.sendToManagerService(managerRequest);
  }

  private void createManagerAuthStep(String sagaId) {
    log.info("Step 2: Creating manager auth for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("CREATE_MANAGER_AUTH", "PENDING",
        "Creating manager auth user", "ROLLBACK_CREATE_MANAGER_AUTH");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    AuthPayload payload = new AuthPayload();
    payload.setAction("CREATE_MANAGER_AUTH");
    payload.setMessageSource("orchestrator");
    payload.setSagaId(sagaId);

    AuthPayloadData data = new AuthPayloadData();
    data.setId((Long) context.get("managerId"));
    data.setName(context.get("name").toString());
    data.setCpf(context.get("cpf").toString());
    data.setEmail((String) context.get("email"));
    data.setPassword((String) context.get("password"));
    data.setRole("GERENTE");

    payload.setData(data);

    sagaProducer.sendToAuthService(payload);
  }

  private void assignAccountToNewManagerStep(String sagaId) {
    log.info("Step 3: Assigning account to new manager for saga {}", sagaId);
    Map<String, Object> context = sagaContexts.get(sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElseThrow();
    SagaStep step = new SagaStep("ASSIGN_ACCOUNT_TO_NEW_MANAGER", "PENDING",
        "Assigning account to new manager", "REVERT_ACCOUNT_ASSIGNMENT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    Map<String, Object> accountRequest = new HashMap<>();
    accountRequest.put("sagaId", sagaId);
    accountRequest.put("action", "ASSIGN_ACCOUNT_TO_NEW_MANAGER");
    accountRequest.put("newManagerId", context.get("managerId"));

    sagaProducer.sendToAccountService(accountRequest);
  }

  private void completeManagerCreationSaga(String sagaId) {
    log.info("Completing CREATE_MANAGER saga {}", sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElse(null);
    if (saga == null) {
      log.error("Saga not found in database for sagaId: {}. Cannot complete saga.", sagaId);
      sagaContexts.remove(sagaId);
      return;
    }

    saga.setStatus("COMPLETED");
    sagaRepository.save(saga);

    Map<String, Object> context = sagaContexts.get(sagaId);

    if (context != null) {
      System.out.println("========================================");
      System.out.println("MANAGER CREATED - SAGA: " + sagaId);
      System.out.println("========================================");
      System.out.println("Manager ID: " + context.get("managerId"));
      System.out.println("Nome: " + context.get("name"));
      System.out.println("CPF: " + context.get("cpf"));
      System.out.println("Email: " + context.get("email"));
      System.out.println("Type: " + context.get("type"));
      System.out.println("========================================");
    }

    // Do not remove context yet - API Gateway needs it for response
  }

  private void completeSaga(String sagaId , String title) {
    log.info("Step 7: Completing saga {}", sagaId);

    Saga saga = sagaRepository.findById(sagaId).orElse(null);
    if (saga == null) {
      log.error("Saga not found in database for sagaId: {}. Cannot complete saga.", sagaId);
      sagaContexts.remove(sagaId);
      return;
    }

    saga.setStatus("COMPLETED");
    sagaRepository.save(saga);

    Map<String, Object> context = sagaContexts.get(sagaId);

    if (context != null) {
      // PRINT NO TERMINAL com as credenciais
      System.out.println("========================================");
      System.out.println(title + " - SAGA: " + sagaId);
      System.out.println("========================================");
      System.out.println("Cliente ID: " + context.get("clientId"));
      System.out.println("Nome: " + context.get("name"));
      System.out.println("CPF: " + context.get("cpf"));
      System.out.println("Email: " + context.get("email"));
      System.out.println("Conta: " + context.get("accountNumber"));
      System.out.println("Gerente ID: " + context.get("managerId"));

      String password = (String) context.get("generatedPassword");
      if (password != null) {
        System.out.println("Senha gerada: " + password);
      } else {
        System.out.println("Senha: Verifique com o administrador");
      }
      System.out.println("========================================");
    }

    // Limpar contexto
    sagaContexts.remove(sagaId);
  }

  @Transactional
  public void executeCompensation(String sagaId, SagaResult failedResult) {
    log.error("Saga {} falhou no step {}: {}", sagaId, failedResult.getAction(), failedResult.getError());

    Saga saga = sagaRepository.findById(sagaId).orElse(null);
    if (saga == null) {
      log.error("Saga {} not found for compensation", sagaId);
      return;
    }

    saga.setStatus("FAILED");
    sagaRepository.save(saga);

    Map<String, Object> context = sagaContexts.get(sagaId);
    if (context == null) {
      log.warn("No context found for saga {}, cannot compensate", sagaId);
      return;
    }

    // Compensação baseada em qual step falhou
    String failedAction = failedResult.getAction();

    log.info("Executing compensation for failed action: {}", failedAction);

    // Compensar na ordem reversa
    if (failedAction.contains("CREATE_RESULT") || failedAction.contains("CREATE_USER")) {
      // Falhou ao criar usuário ou depois - compensar tudo
      compensateUpdateAccountStatus(context);
      compensateApproveClient(context);
      compensateCreateAccount(context);
      compensateAssignManager(context);
      compensateCreateClient(context);
    } else if (failedAction.contains("UPDATE_ACCOUNT_STATUS")) {
      // Falhou ao ativar conta
      compensateApproveClient(context);
      compensateCreateAccount(context);
      compensateAssignManager(context);
      compensateCreateClient(context);
    } else if (failedAction.contains("APPROVE_CLIENT")) {
      // Falhou ao aprovar cliente
      compensateCreateAccount(context);
      compensateAssignManager(context);
      compensateCreateClient(context);
    } else if (failedAction.contains("CREATE_ACCOUNT")) {
      // Falhou ao criar conta
      compensateAssignManager(context);
      compensateCreateClient(context);
    } else if (failedAction.contains("ASSIGN_MANAGER")) {
      // Falhou ao atribuir gerente
      compensateCreateClient(context);
    }

    System.out.println("========================================");
    System.out.println("SAGA FALHOU: " + sagaId);
    System.out.println("========================================");
    System.out.println("Motivo: " + failedResult.getError());
    System.out.println("Step que falhou: " + failedAction);
    System.out.println("Compensação executada");
    System.out.println("========================================");

    sagaContexts.remove(sagaId);
  }

  private void compensateCreateClient(Map<String, Object> context) {
    log.info("Compensating: Deleting client");
    ClientMessageRequest request = new ClientMessageRequest();
    request.setSagaId((String) context.get("sagaId"));
    request.setAction("DELETE_CLIENT");
    request.setClientId((Long) context.get("clientId"));
    sagaProducer.sendToClientService(request);
  }

  private void compensateAssignManager(Map<String, Object> context) {
    log.info("Compensating: Unassigning manager");
    Map<String, Object> managerRequest = new HashMap<>();
    managerRequest.put("sagaId", context.get("sagaId"));
    managerRequest.put("action", "UNASSIGN_MANAGER");
    managerRequest.put("managerId", context.get("managerId"));
    sagaProducer.sendToManagerService(managerRequest);
  }

  private void compensateCreateAccount(Map<String, Object> context) {
    log.info("Compensating: Rejecting account");
    AccountSagaEvent event = new AccountSagaEvent();
    event.setSagaId((String) context.get("sagaId"));
    event.setAction("UPDATE_ACCOUNT_STATUS");
    event.setClientId((Long) context.get("clientId"));
    event.setIsApproved(false);
    sagaProducer.sendToAccountService(event);
  }

  private void compensateApproveClient(Map<String, Object> context) {
    log.info("Compensating: Rejecting client");
    ClientMessageRequest request = new ClientMessageRequest();
    request.setSagaId((String) context.get("sagaId"));
    request.setAction("REJECT_CLIENT");
    request.setClientId((Long) context.get("clientId"));
    request.setRejectionReason("Rollback de saga - falha em step posterior");
    sagaProducer.sendToClientService(request);
  }

  private void compensateUpdateAccountStatus(Map<String, Object> context) {
    log.info("Compensating: Deactivating account");
    AccountSagaEvent event = new AccountSagaEvent();
    event.setSagaId((String) context.get("sagaId"));
    event.setAction("UPDATE_ACCOUNT_STATUS");
    event.setClientId((Long) context.get("clientId"));
    event.setIsApproved(false);
    sagaProducer.sendToAccountService(event);
  }

  private String convertMapToString(Map<String, Object> data) {
    if (data == null) {
      return null;
    }
    try {
      return data.toString();
    } catch (Exception e) {
      return null;
    }
  }

  public Map<String, Object> getSagaContext(String sagaId) {
    return sagaContexts.get(sagaId);
  }

  public void removeSagaContext(String sagaId) {
    sagaContexts.remove(sagaId);
  }
}
