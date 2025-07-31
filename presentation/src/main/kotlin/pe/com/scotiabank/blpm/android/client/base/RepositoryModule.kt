package pe.com.scotiabank.blpm.android.client.base

import dagger.Module
import dagger.Provides
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.data.repository.*
import pe.com.scotiabank.blpm.android.data.repository.foreignexchange.ForeignExchangeBusinessDataRepository
import pe.com.scotiabank.blpm.android.data.repository.foreignexchange.ForeignExchangeRepository
import pe.com.scotiabank.blpm.android.data.repository.foreignexchange.ForeignExchangeDataRepository
import pe.com.scotiabank.blpm.android.data.repository.products.*
import pe.com.scotiabank.blpm.android.data.repository.products.accountstatements.AccountStatementBusinessDataRepository
import pe.com.scotiabank.blpm.android.data.repository.products.accountstatements.AccountStatementRepository
import pe.com.scotiabank.blpm.android.data.repository.superaccount.SuperAccountDetailRepository
import pe.com.scotiabank.blpm.android.data.repository.superaccount.SuperAccountRepository

@Module
class RepositoryModule {

    @Provides
    fun provideProductRepository(
        appModel: AppModel,
        joyPersonRepository: PersonRepository,
        joyBusinessRepository: BusinessRepository,
    ): ProductRepository {
        if (DashboardType.BUSINESS == appModel.dashboardType) {
            return joyBusinessRepository
        }
        return joyPersonRepository
    }

    @Provides
    fun provideProductDetailRepository(
        appModel: AppModel,
        joyRepository: ProductDetailDataRepository,
        joyBusinessRepository: ProductDetailBusinessDataRepository,
    ): ProductDetailRepository {
        if (DashboardType.BUSINESS == appModel.dashboardType) {
            return joyBusinessRepository
        }
        return joyRepository
    }

    @Provides
    fun provideMovementRepository(
        appModel: AppModel,
        joyRepository: MovementDataRepository,
        joyBusinessRepository: MovementBusinessDataRepository,
    ): MovementRepository {
        if (DashboardType.BUSINESS == appModel.dashboardType) {
            return joyBusinessRepository
        }
        return joyRepository
    }

    @Provides
    fun providePaymentRepository(
        appModel: AppModel,
        joyRepository: PaymentDataRepository,
        joyBusinessRepository: PaymentBusinessDataRepository,
    ): PaymentRepository {
        if (DashboardType.BUSINESS == appModel.dashboardType) {
            return joyBusinessRepository
        }
        return joyRepository
    }

    @Provides
    fun provideTransferRepository(
        appModel: AppModel,
        joyRepository: TransferDataRepository,
        joyBusinessRepository: TransferBusinessDataRepository,
    ): TransferRepository {
        if (DashboardType.BUSINESS == appModel.dashboardType) {
            return joyBusinessRepository
        }
        return joyRepository
    }

    @Provides
    fun provideLoanRepository(
        appModel: AppModel,
        joyRepository: LoanDataRepository,
        joyBusinessRepository: LoanBusinessDataRepository,
    ): LoanRepository {
        if (DashboardType.BUSINESS == appModel.dashboardType) {
            return joyBusinessRepository
        }
        return joyRepository
    }

    @Provides
    fun provideForeignExchangeRepository(
        appModel: AppModel,
        joyRepository: ForeignExchangeDataRepository,
        joyBusinessRepository: ForeignExchangeBusinessDataRepository,
    ): ForeignExchangeRepository {
        if (DashboardType.BUSINESS == appModel.dashboardType) {
            return joyBusinessRepository
        }
        return joyRepository
    }

    @Provides
    fun providePersonFlowRepository(
        personFlowRepository: PersonFlowRepository,
    ): ProductFlowRepository = personFlowRepository

    @Provides
    fun provideBusinessFlowRepository(
        businessFlowRepository: BusinessFlowRepository,
    ): ProductFlowRepository = businessFlowRepository

    @Provides
    fun provideSuperAccountRepository(
        superAccountRepository: SuperAccountRepository,
    ): SuperAccountDetailRepository = superAccountRepository

    @Provides
    fun provideAccountStatementRepository(
        accountStatementBusinessDataRepository: AccountStatementBusinessDataRepository,
    ): AccountStatementRepository {
        return accountStatementBusinessDataRepository
    }

}
