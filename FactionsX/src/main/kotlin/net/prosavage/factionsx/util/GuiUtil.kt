package net.prosavage.factionsx.util

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.ClickableItem.of
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.Pagination
import fr.minuskube.inv.content.SlotIterator

fun preparePagination(
        pagination: Pagination,
        contents: InventoryContents,
        startCoordinate: Coordinate,
        objectsPerPage: Int,
        nextButton: InterfaceItem,
        previousButton: InterfaceItem,
        iteratorType: SlotIterator.Type,
        items: Collection<ClickableItem>,
        onNext: () -> Unit,
        onPrevious: () -> Unit
): Pagination {
    val (paginationStartRow, paginationStartColumn) = startCoordinate
    pagination.setItemsPerPage(objectsPerPage)
    pagination.setItems(*items.toTypedArray())
    pagination.addToIterator(contents.newIterator(iteratorType, paginationStartRow, paginationStartColumn))

    val (nextButtonRow, nextButtonColumn) = nextButton.coordinate
    contents.set(nextButtonRow, nextButtonColumn, paginatedButton(nextButton, onNext))

    val (previousButtonRow, previousButtonColumn) = previousButton.coordinate
    contents.set(previousButtonRow, previousButtonColumn, paginatedButton(previousButton, onPrevious))
    return pagination
}

internal fun paginatedButton(
        button: InterfaceItem,
        then: () -> Unit
) = of(button.displayItem.buildItem()) { then() }