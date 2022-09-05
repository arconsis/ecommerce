import axios from 'axios';
import util from 'util';
import { IAddressValidationRequest } from "../../../presentation/functions/dto/IAddressValidationRequest";
import { IAddressValidationResponseDto } from "./dto/IAddressValidationResponseDto";
import configuration from "../../../configuration";

interface IAddressesRemoteStoreFactory {
    init(): IAddressesRemoteStore;
}

export interface IAddressesRemoteStore {
    validateAddress(address: IAddressValidationRequest): Promise<Boolean>,
}

export const addressesRemoteStoreFactory: IAddressesRemoteStoreFactory = {
    init() {
        async function validateAddress(address: IAddressValidationRequest): Promise<Boolean> {
            try {
                console.log(`Trying to validate address ${JSON.stringify({
                    name: `${address.firstName} ${address.lastName}`,
                    street1: `${address.houseNumber} ${address.address}`,
                    country: address.countryCode,
                    zip: address.postalCode,
                    city: address.city,
                    state: address.state,
                    validate: true,
                })}`);
                const res = await axios.post<IAddressValidationResponseDto>(`${configuration.shippo.basePath}/addresses`, {
                    name: `${address.firstName} ${address.lastName}`,
                    street1: `${address.houseNumber} ${address.address}`,
                    country: address.countryCode,
                    zip: address.postalCode,
                    city: address.city,
                    state: address.state,
                    validate: true,
                }, {
                    headers: {
                        Authorization: `ShippoToken ${configuration.shippo.apiKey}`,
                        Accept: 'application/json',
                        'Content-Type': 'application/json',
                    },
                });
                console.log('response status is: ', res.status);
                console.log("validation response is: ", util.inspect(res.data.validation_results, false, null, true /* enable colors */))
                console.log('is address valid: ', res.data.validation_results.is_valid);
                if (!res.data.validation_results.is_valid) {
                    throw new Error(`Address not valid because of: ${res.data.validation_results.messages[0]?.code}`);
                }
                return true;
            } catch (error) {
                console.error(`Error when trying to validate address ${JSON.stringify(address)} because of: `, error);
                throw error
            }
        }

        return {
            validateAddress
        }
    }
}