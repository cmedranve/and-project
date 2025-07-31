package pe.com.scotiabank.blpm.android.ui.list.composite

import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch.UiEntityOfMaterialSearch
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.navigation.UiEntityOfNavigation
import pe.com.scotiabank.blpm.android.ui.list.items.page.UiEntityOfPage

class MutableCompositeOfAppBarAndMain(
    override val toolbarCompounds: MutableList<UiCompoundOfSingle<UiEntityOfToolbar>> = mutableListOf(),
    override val mainCompoundsById: LinkedHashMap<Long, UiCompound<*>> = LinkedHashMap(),
    override val mainDecorationCompounds: MutableList<DecorationCompound> = mutableListOf(),
    override val pageEntities: MutableList<UiEntityOfPage> = mutableListOf(),
    override val navigationCompoundsById: LinkedHashMap<Long, UiCompoundOfSingle<UiEntityOfNavigation>> = LinkedHashMap(),
    override val searchBarCompounds: MutableList<UiCompoundOfSingle<UiEntityOfMaterialSearch>> = mutableListOf(),
    override val resultCompoundsById: LinkedHashMap<Long, UiCompound<*>> = LinkedHashMap(),
): CompositeOfAppBarAndMain
