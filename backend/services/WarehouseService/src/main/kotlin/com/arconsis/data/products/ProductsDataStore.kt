package com.arconsis.data.products

import com.arconsis.domain.products.CreateProduct
import com.arconsis.domain.products.Product
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.NotFoundException


@ApplicationScoped
class ProductsDataStore: PanacheRepository<ProductEntity> {
	fun getProduct(productId: UUID): Uni<Product> {
		return find("productId", productId)
			.firstResult<ProductEntity?>()
			.map {
				if (it == null) {
					throw NotFoundException("No results found");
				}
				it.toProduct()
			}
	}

	fun createProduct(newProduct: CreateProduct): Uni<Product> {
		val eEntity = newProduct.toProductEntity()
		return persist(eEntity)
			.map {
				it.toProduct()
			}
	}
}
