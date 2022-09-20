export class Address {
    readonly firstName: string;

    readonly lastName: string;

    readonly address: string;

    readonly houseNumber: string;

    readonly countryCode: string;

    readonly postalCode: string;

    readonly city: string;

    readonly phone: string;

    readonly state: string;

    constructor(
        firstName: string,
        lastName: string,
        address: string,
        houseNumber: string,
        countryCode: string,
        postalCode: string,
        city: string,
        phone: string,
        state: string
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.houseNumber = houseNumber;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
        this.city = city;
        this.phone = phone;
        this.state = state;
    }
}