package com.arconsis.presentation.http.products

import com.arconsis.domain.products.Product
import com.arconsis.domain.products.ProductsService
import com.arconsis.presentation.http.products.dto.CreateProductDto
import com.arconsis.presentation.http.products.dto.toCreateProduct
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*

@ApplicationScoped
@Path("/products")
class ProductsResource(val productsService: ProductsService) {
	@POST
	@Path("/")
	fun createProduct(createProductDto: CreateProductDto): Uni<Product> {
		return productsService.createProduct(createProductDto.toCreateProduct())
	}

	@GET
	@Path("/{productId}")
	fun getProduct(@PathParam("productId") productId: UUID): Uni<Product?> {
		return productsService.getProduct(productId)
	}
}