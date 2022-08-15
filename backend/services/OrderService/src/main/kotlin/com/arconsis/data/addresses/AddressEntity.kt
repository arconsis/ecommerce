package com.arconsis.data.addresses

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.addresses.SupportedCountryCode
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.util.*
import javax.persistence.*

@NamedQueries(
    NamedQuery(
        name = AddressEntity.UNSET_BASKET_ADDRESSES_BILLING_ADDRESS_FLAG,
        query = """ update addresses a 
                    set a.isBilling  = false
                    where a.basketId = :basketId
                        """
    ),
    NamedQuery(
        name = AddressEntity.UNSET_BASKET_ADDRESSES_IS_SELECTED_ADDRESS_FLAG,
        query = """ update addresses a 
                    set a.isSelected = false
                    where a.basketId = :basketId
                        """
    ),
    NamedQuery(
        name = AddressEntity.SET_IS_BILLING_ADDRESS_FLAG,
        query = """ update addresses a 
                    set a.isBilling = true
                    where a.addressId = :addressId
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
    @Column(name = "basket_id")
    var basketId: UUID,

    @Column
    var name: String,

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

    @Column(name = "is_billing")
    var isBilling: Boolean,

    @Column(name = "is_selected")
    var isSelected: Boolean,
) {
    companion object {
        const val UNSET_BASKET_ADDRESSES_BILLING_ADDRESS_FLAG = "unset_billing_address_flag"
        const val UNSET_BASKET_ADDRESSES_IS_SELECTED_ADDRESS_FLAG = "unset_is_selected_address_flag"
        const val SET_IS_BILLING_ADDRESS_FLAG = "set_billing_address_flag"
    }
}
