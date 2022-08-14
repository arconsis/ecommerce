package com.arconsis.data.addresses

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.common.ADDRESS_ID
import com.arconsis.data.common.USER_ID
import com.arconsis.domain.addresses.CountryCode
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.util.*
import javax.persistence.*

@NamedQueries(
    NamedQuery(
        name = AddressEntity.LIST_USER_ADDRESSES,
        query = """ select a from addresses a
                    where a.userId = :$USER_ID
                        """
    ),
    NamedQuery(
        name = AddressEntity.GET_BILLING_ADDRESS,
        query = """ select a from addresses a
                    where a.userId = :$USER_ID and a.isBilling = true
                        """
    ),
    NamedQuery(
        name = AddressEntity.DELETE_BILLING_ADDRESS,
        query = """ update addresses a 
                    set a.isBilling  = case a.isBilling
                    when true then false else false end
                    where a.userId = :$USER_ID
                        """
    ),
    NamedQuery(
        name = AddressEntity.UNSET_USER_ADDRESSES_BILLING_ADDRESS_FLAG,
        query = """ update addresses a 
                    set a.isBilling  = false
                    where a.userId = :$USER_ID
                        """
    ),
    NamedQuery(
        name = AddressEntity.UNSET_USER_ADDRESSES_IS_SELECTED_ADDRESS_FLAG,
        query = """ update addresses a 
                    set a.isSelected = false
                    where a.userId = :$USER_ID
                        """
    ),
    NamedQuery(
        name = AddressEntity.SET_IS_BILLING_ADDRESS_FLAG,
        query = """ update addresses a 
                    set a.isBilling = true
                    where a.addressId = :$ADDRESS_ID
                        """
    ),
)
@Entity(name = "addresses")
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
)
class AddressEntity(
    @Id
    @GeneratedValue
    @Column(name = "address_id")
    var addressId: UUID? = null,

    // FK
    @Column(name = "user_id")
    var userId: UUID,

    @Column
    var name: String,

    @Column
    var address: String,

    @Column(name = "house_number")
    var houseNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "country_code")
    @Type(type = "pgsql_enum")
    var countryCode: CountryCode,

    @Column(name = "postal_code")
    var postalCode: String,

    @Column
    var city: String,

    @Column
    var phone: String,

    @Column(name = "is_billing")
    var isBilling: Boolean,

    @Column(name = "is_selected")
    var isSelected: Boolean,
) {
    companion object {
        const val LIST_USER_ADDRESSES = "list_user_addresses"
        const val GET_BILLING_ADDRESS = "get_billing_address"
        const val DELETE_BILLING_ADDRESS = "delete_billing_address"
        const val UNSET_USER_ADDRESSES_BILLING_ADDRESS_FLAG = "unset_billing_address_flag"
        const val UNSET_USER_ADDRESSES_IS_SELECTED_ADDRESS_FLAG = "unset_is_selected_address_flag"
        const val SET_IS_BILLING_ADDRESS_FLAG = "set_billing_address_flag"
    }
}
