package archiveasia.jp.co.hakenman.viewmodel

import androidx.lifecycle.ViewModel
import archiveasia.jp.co.hakenman.manager.WorksheetManager

class WorksheetListViewModel : ViewModel() {
    val worksheetList = WorksheetManager.tempWorksheetList


}