package com.arconsis.presentation.http.products

import com.arconsis.domain.products.Product
import com.arconsis.domain.products.ProductsService
import com.arconsis.presentation.http.products.dto.request.CreateProductDto
import com.arconsis.presentation.http.products.dto.request.toCreateProduct
import com.arconsis.presentation.http.products.dto.response.ListProductsPaginationResponseDto
import com.arconsis.presentation.http.products.dto.response.ListProductsResponseDto
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
	@Path("/")
	fun listProducts(@QueryParam("offset") offset: Int?, @QueryParam("limit") limit: Int?, @QueryParam("search") search: String?): Uni<ListProductsResponseDto> {
		val (queryOffset, queryLimit) = getPaginationSize(offset, limit)
		return productsService.listProducts(queryOffset, queryLimit, search)
			.map { (products, total) ->
				ListProductsResponseDto(
					products = products,
					pagination = ListProductsPaginationResponseDto(
						offset = queryOffset,
						limit = queryLimit,
						total = total
					)
				)
			}
	}

	@GET
	@Path("/{productId}")
	fun getProduct(@PathParam("productId") productId: UUID): Uni<Product?> {
		return productsService.getProduct(productId)
	}

	private fun getPaginationSize(offset: Int?, limit: Int?): Pair<Int, Int> {
		val queryOffset = offset ?: DEFAULT_OFFSET
		val queryLimit = when (limit) {
			null -> DEFAULT_LIMIT
			in MIN_LIMIT..MAX_LIMIT -> limit
			else -> DEFAULT_LIMIT
		}
		return queryOffset to queryLimit
	}

	companion object {
		private const val MAX_LIMIT = 100
		private const val MIN_LIMIT = 1
		private const val DEFAULT_LIMIT = 33
		private const val DEFAULT_OFFSET = 0
	}
}