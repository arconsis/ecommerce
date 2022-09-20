export interface IAddressValidationResponseDto {
    validation_results: IAddressValidationResultResponseDto,
}

export interface IAddressValidationResultResponseDto {
    is_valid: boolean,
    messages: IAddressValidationErrorMessageResponseDto[]
}

export interface IAddressValidationErrorMessageResponseDto {
    code: string,
    text: string
}