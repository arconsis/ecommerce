import { IAddressValidationRequest } from '../../presentation/functions/dto/IAddressValidationRequest';
import { IRepositories } from '../../common/interfaces/IRepositories';

interface IAddressesServiceFactory {
  init(repositories: IRepositories): IAddressesService;
}

export interface IAddressesService {
  validateAddress(address: IAddressValidationRequest): Promise<Boolean>
}

export const addressServiceFactory: IAddressesServiceFactory = {
  init(repositories: IRepositories) {
    async function validateAddress(address: IAddressValidationRequest): Promise<Boolean> {
      return repositories.addressesRepository.validateAddress(address);
    }

    return {
      validateAddress,
    };
  },
};
