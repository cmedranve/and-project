package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.FrequentOperationService
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.OperationMatcher
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.AdapterFactoryOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.lang.ref.WeakReference

class ComposerOfSuccessfulPayment(
    weakResources: WeakReference<Resources?>,
    dividerPositions: List<Int>,
    paddingEntity: UiEntityOfPadding,
    paddingOfLeftImage: UiEntityOfPadding,
    receiver: InstanceReceiver,
    private val matcher: OperationMatcher,
): FrequentOperationService {

    private val converter: ConverterForSuccessfulPayment = ConverterForSuccessfulPayment(
        weakResources = weakResources,
        dividerPositions = dividerPositions,
        paddingEntity = paddingEntity,
        paddingOfLeftImage = paddingOfLeftImage,
        receiver = receiver,
    )

    private val itemEntities: MutableList<UiEntityOfDoubleEndedImage<FrequentOperationModel>> = mutableListOf()
    override val quantity: Int by itemEntities::size

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfDoubleEndedImage<FrequentOperationModel>> {

        val adapterFactory: AdapterFactoryOfDoubleEndedImage<FrequentOperationModel> = AdapterFactoryOfDoubleEndedImage()
        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    override fun add(frequentOperation: FrequentOperationModel) {
        val newEntity: UiEntityOfDoubleEndedImage<FrequentOperationModel> = converter.toUiEntityOfDoubleEndedImage(
            frequentOperation = frequentOperation,
            id = randomLong(),
        )
        itemEntities.add(newEntity)
    }

    override fun edit(frequentOperation: FrequentOperationModel) {
        val indexOfOldEntity: Int = itemEntities
            .indexOfFirst { entity -> matcher.isMatching(entity.data, frequentOperation) }
        if (NOT_FOUND == indexOfOldEntity) return

        val oldEntity = itemEntities[indexOfOldEntity]
        val newEntity: UiEntityOfDoubleEndedImage<FrequentOperationModel> = converter.toUiEntityOfDoubleEndedImage(
            frequentOperation = frequentOperation,
            id = oldEntity.id,
        )
        itemEntities[indexOfOldEntity] = newEntity
    }

    override fun remove(frequentOperation: FrequentOperationModel) {
        itemEntities.removeIf { oldEntity -> matcher.isMatching(oldEntity.data, frequentOperation) }
    }

    override fun clear() {
        itemEntities.clear()
    }

    companion object {

        private val NOT_FOUND: Int
            get() = -1
    }
}