package pe.com.scotiabank.blpm.android.ui.list.composite

import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.UiEntityOfMaterialSearch
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.navigation.UiEntityOfNavigation
import pe.com.scotiabank.blpm.android.ui.list.items.page.UiEntityOfPage

interface CompositeOfAppBarAndMain {

    val toolbarCompounds: List<UiCompoundOfSingle<UiEntityOfToolbar>>
    val mainCompoundsById: Map<Long, UiCompound<*>>
    val mainDecorationCompounds: List<DecorationCompound>
    val pageEntities: List<UiEntityOfPage>
    val navigationCompoundsById: Map<Long, UiCompoundOfSingle<UiEntityOfNavigation>>
    val searchBarCompounds: List<UiCompoundOfSingle<UiEntityOfMaterialSearch>>
    val resultCompoundsById: Map<Long, UiCompound<*>>
}
