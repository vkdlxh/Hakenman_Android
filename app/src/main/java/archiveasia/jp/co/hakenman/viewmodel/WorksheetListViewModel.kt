package archiveasia.jp.co.hakenman.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import archiveasia.jp.co.hakenman.extension.SingleLiveEvent
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.Worksheet

class WorksheetListViewModel : ViewModel() {
    val worksheetList = WorksheetManager.tempWorksheetList

    private val _showUpdateDialog = SingleLiveEvent<Worksheet>()
    val showUpdateDialog: LiveData<Worksheet> = _showUpdateDialog

    fun createWorksheet(year: String, month: String) {
        // TODO: 検討
        val yearMonth = if (month.length < 2) {
            year + 0 + month
        } else {
            "$year$month"
        }

        val worksheet = WorksheetManager.createWorksheet(yearMonth)
        if (WorksheetManager.isAlreadyExistWorksheet(yearMonth)) {
            _showUpdateDialog.value = worksheet
        } else {
            WorksheetManager.tempAddWorksheetToJsonFile(worksheet)
        }
    }

    fun updateWorksheet(worksheet: Worksheet) {
        WorksheetManager.tempUpdateWorksheet(worksheet)
    }

    fun removeWorksheet(index: Int) {
        WorksheetManager.tempRemoveWorksheet(index)
    }

}