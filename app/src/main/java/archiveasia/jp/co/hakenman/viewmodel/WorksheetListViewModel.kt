package archiveasia.jp.co.hakenman.viewmodel

import androidx.lifecycle.ViewModel
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.Worksheet

class WorksheetListViewModel : ViewModel() {
    val worksheetList = WorksheetManager.tempWorksheetList

    fun updateWorksheet(worksheet: Worksheet) {
        WorksheetManager.tempUpdateWorksheet(worksheet)
    }

}