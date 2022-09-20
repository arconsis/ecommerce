import { IAddressValidationRequest } from "../../../presentation/functions/dto/IAddressValidationRequest";
import { IAddressesRepository } from "../../../domain/addresses/repository";
import { IAddressesRemoteStore } from "./remoteStore";

interface IAddressesRepositoryFactory {
    init(remoteStore: IAddressesRemoteStore): IAddressesRepository;
}

export const addressesRepositoryFactory: IAddressesRepositoryFactory = {
    init(remoteStore: IAddressesRemoteStore) {
        async function validateAddress(address: IAddressValidationRequest): Promise<Boolean> {
            return remoteStore.validateAddress(address);
        }

        return {
            validateAddress
        }
    }
}