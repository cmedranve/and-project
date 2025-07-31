package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.form

interface ContactQueryTemplate {

    fun onContactId(contactId: String)

    fun onFirstName(firstName: String)

    fun onLastName(lastName: String)

    fun onPhoneNumber(phoneNumber: String)
}
