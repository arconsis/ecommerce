import { IAddressValidationRequest } from "../../presentation/functions/dto/IAddressValidationRequest";

export interface IAddressesRepository {
    validateAddress(address: IAddressValidationRequest): Promise<Boolean>;
}