package com.example.plutoaccessories.network

import com.example.plutoaccessories.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // 🔹 Register user
    @FormUrlEncoded
    @POST("register_user.php")
    fun registerUser(
        @Field("nama") nama: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("alamat") alamat: String,
        @Field("no_hp") noHp: String
    ): Call<ApiResponse>

    // 🔹 Login user
    @FormUrlEncoded
    @POST("login_user.php")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // 🔹 Ambil daftar user (khusus admin)
    @GET("get_user.php")
    fun getAllUsers(): Call<List<UserModel>>

    // 🔹 Produk
    @GET("get_products.php")
    fun getProducts(): Call<List<Product>>

    @GET("get_product_detail.php")
    fun getProductDetail(@Query("id_produk") idProduk: Int): Call<Product>

    @FormUrlEncoded
    @POST("add_product.php")
    fun addProduct(
        @Field("nama_produk") name: String,
        @Field("harga") price: Int,
        @Field("deskripsi") description: String,
        @Field("gambar") image: String
    ): Call<Product>

    @FormUrlEncoded
    @POST("edit_product.php")
    fun editProduct(
        @Field("id_produk") id: Int,
        @Field("nama_produk") name: String,
        @Field("harga") price: Int,
        @Field("deskripsi") description: String,
        @Field("gambar") image: String?
    ): Call<Product>

    @FormUrlEncoded
    @POST("delete_product.php")
    fun deleteProduct(
        @Field("id_produk") id: Int
    ): Call<ApiResponse>

    // 🔹 Order
    @FormUrlEncoded
    @POST("insert_order.php")
    fun insertOrder(
        @Field("id_user") idUser: Int,
        @Field("total_harga") totalHarga: Double,
        @Field("items") itemsJson: String,
        @Field("alamat") address: String,
        @Field("metode_pembayaran") paymentMethod: String,
        @Field("opsi_pengiriman") shippingMethod: String
    ): Call<ApiResponse>

    // 🔹 Orders history
    @GET("get_orders.php")
    fun getOrders(@Query("id_user") idUser: Int): Call<List<Order>>
}
