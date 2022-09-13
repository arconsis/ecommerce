package com.arconsis.data.products

import com.arconsis.data.common.asPair
import com.arconsis.data.productmedia.ProductMediaEntity
import com.arconsis.domain.products.CreateProduct
import com.arconsis.domain.products.Product
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Root

@ApplicationScoped
class ProductsDataStore(private val sessionFactory: Mutiny.SessionFactory) {
	fun getProduct(productId: UUID, session: Session): Uni<Product?> {
		return session.createNamedQuery<ProductEntity>(ProductEntity.GET_BY_PRODUCT_ID)
			.setParameter("productId", productId)
			.singleResultOrNull
			.map { it.toProduct() }
	}

	fun createProduct(newProduct: CreateProduct, slug: String, sku: String, session: Session): Uni<Product> {
		val entity = newProduct.toProductEntity(slug, sku)
		return session.persist(entity)
			.flatMap {
				entity.gallery = newProduct.gallery.map {
					ProductMediaEntity(
						productId = entity.productId!!,
						original = it.original,
						thumbnail = it.thumbnail,
						isPrimary = it.isPrimary,
						type = it.type
					)
				}.toMutableList()
				session.persist(entity)
			}
			.map { entity.toProduct() }
	}

	fun listProducts(offset: Int, limit: Int, search: String?): Uni<Pair<List<Product>, Int>> {
		return sessionFactory.withTransaction { session, _ ->
			Uni.combine().all().unis(
				listProductsByQuery(offset, limit, search, session),
				countProducts(search, session),
			).asPair()
				.map { (list, total) ->
					list.map { entity -> entity.toProduct() } to total
				}
		}
	}

	private fun getSearchParamsToListProducts(search: String?): List<Pair<String, String>> {
		val searchParams = search?.split(";")
			?.map { searchParam ->
				val keyValue = searchParam.split(":")
				Pair(keyValue[0], keyValue[1])
			} ?: emptyList()
		return searchParams
	}

	private fun listProductsByQuery(offset: Int, limit: Int, search: String?, session: Session): Uni<List<ProductEntity>> {
		val cb = sessionFactory.criteriaBuilder
		val criteria = cb.createQuery(ProductEntity::class.java)
		val root = criteria.from(ProductEntity::class.java)
		val searchParams = getSearchParamsToListProducts(search)
		searchParams.forEach { (key, value) ->
			criteria.where(cb.like(cb.lower(root[key]), "${value.lowercase(Locale.getDefault())}%"))
		}
		val query = session.createQuery(criteria)
		query.firstResult = offset
		query.maxResults = limit
		return query.resultList
	}

	private fun countProducts(search: String?, session: Session): Uni<Int> {
		val cb = sessionFactory.criteriaBuilder
		val criteria = cb.createQuery(Long::class.java)
		val root: Root<ProductEntity> = criteria.from(ProductEntity::class.java)
		val searchParams = getSearchParamsToListProducts(search)
		searchParams.forEach { (key, value) ->
			criteria.where(cb.like(cb.lower(root[key]), "${value.lowercase(Locale.getDefault())}%"))
		}
		criteria.select(cb.count(root))
		return session.createQuery(criteria)
			.resultList
			.map {
				it.firstOrNull()?.toInt() ?: 0
			}
	}
}
