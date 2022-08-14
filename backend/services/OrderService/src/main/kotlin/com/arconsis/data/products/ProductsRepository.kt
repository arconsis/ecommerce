package com.arconsis.data.products

import com.arconsis.common.body
import com.arconsis.data.products.dto.ProductDto
import com.arconsis.data.products.dto.toProduct
import com.arconsis.domain.products.Product
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException

@ApplicationScoped
class ProductsRepository(
	@RestClient private val productsRemoteStore: ProductsRemoteStore
) {

	fun getProduct(productId: UUID): Uni<Product> {
		return productsRemoteStore.getProduct(productId).map {
			when (it.status) {
				200 -> it.body<ProductDto>()?.toProduct()
				else -> throw BadRequestException("Product with id: $productId not found")
			}
		}
	}
}