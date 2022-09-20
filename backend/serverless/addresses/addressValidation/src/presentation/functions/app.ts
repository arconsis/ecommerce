import { Context, APIGatewayProxyResult, APIGatewayEvent } from 'aws-lambda';
import { IAddressValidationRequest } from './dto/IAddressValidationRequest';
import { addressesRepositoryFactory } from '../../data/repositories/addresses';
import { addressesRemoteStoreFactory } from '../../data/repositories/addresses/remoteStore';
import { addressServiceFactory } from '../../domain/addresses/service';

const addressesRemoteStore = addressesRemoteStoreFactory.init();
const addressesRepository = addressesRepositoryFactory.init(addressesRemoteStore);
const addressService = addressServiceFactory.init({ addressesRepository });

export const lambdaHandler = async (event: APIGatewayEvent, context: Context): Promise<APIGatewayProxyResult> => {
  try {
    console.log(`Event: ${JSON.stringify(event, null, 2)}`);
    console.log(`Context: ${JSON.stringify(context, null, 2)}`);
    if (typeof event.body !== 'string') {
      throw new Error('address validation body not string');
    }
    const addressValidationRequest: IAddressValidationRequest = JSON.parse(event.body);
    await addressService.validateAddress(addressValidationRequest);
    return {
      statusCode: 200,
      body: JSON.stringify({
        message: 'Address is valid',
      }),
    };
  } catch (error) {
    return {
      statusCode: 400,
      body: JSON.stringify({
        // @ts-ignore
        error: error.message,
      }),
    };
  }
};
