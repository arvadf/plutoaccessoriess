package com.example.plutoaccessories.network

import com.example.plutoaccessories.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // region Users
    @GET("users")
    fun loginUser(
        @Query("select") select: String = "*",
        @Query("email") emailFilter: String,
        @Query("password") passwordFilter: String,
        @Query("limit") limit: Int = 1
    ): Call<List<UserModel>>

    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("users")
    fun registerUser(
        @Body user: UserModel
    ): Call<List<UserModel>>

    @GET("users")
    fun getUsers(
        @Query("select") select: String = "*"
    ): Call<List<UserModel>>
    // endregion

    // region Products
    @GET("products")
    fun getProducts(
        @Query("select") select: String = "*"
    ): Call<List<Product>>

    @GET("products")
    fun getProductDetail(
        @Query("id_produk") idFilter: String,
        @Query("select") select: String = "*"
    ): Call<List<Product>>

    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("products")
    fun addProduct(
        @Body product: Product
    ): Call<List<Product>>

    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @PATCH("products")
    fun editProduct(
        @Query("id_produk") idFilter: String,
        @Body product: Map<String, @JvmSuppressWildcards Any?>
    ): Call<List<Product>>

    @DELETE("products")
    fun deleteProduct(
        @Query("id_produk") idFilter: String
    ): Call<Unit>
    // endregion

    // region Orders
    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("orders")
    fun insertOrder(
        @Body order: Order
    ): Call<List<Order>>

    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("order_items")
    fun insertOrderItems(
        @Body items: List<OrderItem>
    ): Call<List<OrderItem>>

    @GET("orders")
    fun getOrders(
        @Query("select") select: String = "*,order_items(*)",
        @Query("id_user") idUserFilter: String
    ): Call<List<Order>>

    @GET("orders")
    fun getAllOrders(
        @Query("select") select: String = "*,order_items(*,products(*)),users(*)"
    ): Call<List<Order>>

    @GET("orders")
    fun getOrderById(
        @Query("select") select: String = "*,order_items(*,products(*))",
        @Query("id_order") orderFilter: String,
        @Query("limit") limit: Int = 1
    ): Call<List<Order>>

    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @PATCH("orders")
    fun updateOrderStatus(
        @Query("id_order") orderFilter: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): Call<List<Order>>
    // endregion

    // region Transactions & Wallet
    @GET("transactions")
    fun getTransactions(
        @Query("select") select: String = "*",
        @Query("id_user") idUserFilter: String
    ): Call<List<Transaction>>

    @GET("transactions")
    fun getAllTransactions(
        @Query("select") select: String = "*,users(*)"
    ): Call<List<Transaction>>

    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("transactions")
    fun addTransaction(
        @Body transaction: Transaction
    ): Call<List<Transaction>>

    @PATCH("transactions")
    fun updateTransaction(
        @Query("id_transaksi") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<Transaction>

    @GET("wallet")
    fun getWallet(
        @Query("select") select: String = "*",
        @Query("id_user") idUserFilter: String,
        @Query("limit") limit: Int = 1
    ): Call<List<Wallet>>

    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @PATCH("wallet")
    fun updateWallet(
        @Query("id_user") idUserFilter: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): Call<List<Wallet>>

    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("wallet")
    fun createWallet(
        @Body wallet: Wallet
    ): Call<List<Wallet>>

    @POST("rpc/top_up_wallet")
    fun topUpWallet(@Body topUpRequest: TopUpRequest): Call<ApiResponse>
    // endregion
}
