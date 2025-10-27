import {ViaCepResponse} from './signup.service';

export class Address {
  zipCode: string;
  street: string;
  neighborhood: string;
  city: string;
  state: string;
  number: string;
  complement?: string;

  constructor(
    zipCode: string,
    street: string,
    neighborhood: string,
    city: string,
    state: string,
    number: string,
    complement?: string
  ) {
    this.zipCode = zipCode;
    this.street = street;
    this.neighborhood = neighborhood;
    this.city = city;
    this.state = state;
    this.number = number;
    this.complement = complement;
  }

  static fromJson(json?: { [key: string]: any }): Address {
    if (json == null) {
      return Address.empty();
    }
    return new Address(
      json['zipCode'],
      json['street'],
      json['neighborhood'],
      json['city'],
      json['state'],
      json['number'],
      json['complement']
    );
  }

  static empty(): Address {
    return new Address('', '', '', '', '', '', '');
  }

  toJson(): { [key: string]: any } {
    return {
      zipCode: this.zipCode,
      street: this.street,
      neighborhood: this.neighborhood,
      city: this.city,
      state: this.state,
      number: this.number,
      complement: this.complement
    };
  }

  static fromViaCepResponse(data: ViaCepResponse): Address {
    return new Address(
      data.cep,
      data.logradouro,
      data.bairro,
      data.localidade,
      data.uf,
      '0',
      ''
    );
  }
}
