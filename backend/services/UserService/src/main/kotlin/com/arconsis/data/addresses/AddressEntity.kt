package com.arconsis.data.addresses

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.common.USER_ID
import com.arconsis.domain.addresses.SupportedCountryCode
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
        name = AddressEntity.UNSET_USER_ADDRESSES_IS_SELECTED_ADDRESS_FLAG,
        query = """ update addresses a 
                    set a.isSelected = false
                    where a.userId = :$USER_ID
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

    @Column(name = "first_name")
    var firstName: String,

    @Column(name = "last_name")
    var lastName: String,

    @Column
    var address: String,

    @Column(name = "house_number")
    var houseNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "country_code")
    @Type(type = "pgsql_enum")
    var countryCode: SupportedCountryCode,

    @Column(name = "postal_code")
    var postalCode: String,

    @Column
    var city: String,

    @Column
    var phone: String,

    @Column
    var state: String,

    @Column(name = "is_selected")
    var isSelected: Boolean,
) {
    companion object {
        const val LIST_USER_ADDRESSES = "list_user_addresses"
        const val UNSET_USER_ADDRESSES_IS_SELECTED_ADDRESS_FLAG = "unset_is_selected_address_flag"
    }
}
