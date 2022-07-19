package com.arconsis.presentation.http.products

import com.arconsis.domain.products.Product
import com.arconsis.domain.products.ProductsService
import com.arconsis.presentation.http.products.dto.CreateProductDto
import com.arconsis.presentation.http.products.dto.toCreateProduct
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*

@ApplicationScoped
@Path("/products")
class ProductsResource(val productsService: ProductsService) {
	@POST
	@Path("/")
	@ReactiveTransactional
	fun createProduct(createProductDto: CreateProductDto): Uni<Product> {
		return productsService.createProduct(createProductDto.toCreateProduct())
	}

	@GET
	@Path("/{productId}")
	@ReactiveTransactional
	fun getProduct(@PathParam("productId") productId: UUID): Uni<Product?> {
		return productsService.getProduct(productId)
	}
}