package pe.com.scotiabank.blpm.android.client.base.session.entities

enum class TransactionCategory(val id: String, val description: String) {

    TRANSFER(id = "T", description = "Transferencias"),
    PAYMENT(id = "P", description = "Pagos"),
    WITHDRAWAL(id = "R", description = "Retiros"),
    PURCHASE(id = "C", description = "Compras"),
    PUBLIC_SERVICE_PAYMENT(id = "B", description = "Pago de servicios"),
    SUPERMARKET(id = "S", description = "Supermercados"),
    RESTAURANT(id = "F", description = "Restaurantes"),
    EDUCATION(id = "E", description = "Educación"),
    CLOTHING(id = "V", description = "Ropa y calzado"),
    OTHER(id = "O", description = "Otros");

    companion object {

        @JvmStatic
        fun identifyBy(id: String?): TransactionCategory = when (id?.uppercase()) {
            TRANSFER.id -> TRANSFER
            PAYMENT.id -> PAYMENT
            WITHDRAWAL.id -> WITHDRAWAL
            PURCHASE.id -> PURCHASE
            PUBLIC_SERVICE_PAYMENT.id -> PUBLIC_SERVICE_PAYMENT
            SUPERMARKET.id -> SUPERMARKET
            RESTAURANT.id -> RESTAURANT
            EDUCATION.id -> EDUCATION
            CLOTHING.id -> CLOTHING
            else -> OTHER
        }
    }
}
