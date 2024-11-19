package com.example.universalyogalondon.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.data.db.entry.CourseEntry
import com.example.universalyogalondon.data.repository.DatabaseRepository
import com.example.universalyogalondon.helper.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel  @Inject constructor(
    private val repository: DatabaseRepository
): ViewModel() {

    private val _classList by lazy { MutableLiveData<ViewState<List<ClassEntry>>>() }
    val classList: LiveData<ViewState<List<ClassEntry>>> get() = _classList

    fun insertClass(classEntry: ClassEntry) {
        viewModelScope.launch {
            repository.saveClass(classEntry)
        }
    }

    fun getSaveClassList() {
        viewModelScope.launch {
            repository.getClassList()
                .map { recentList ->
                    ViewState.Success(recentList)
                }
                .catch {
                }
                .collect { result ->
                    _classList.postValue(result)
                }
        }
    }

    fun deleteAllClass(){
        viewModelScope.launch {
            repository.deleteAllClass()
        }
    }
    fun deleteByClassId(classId : Int) {
        viewModelScope.launch {
            repository.deleteByClassId(classId)
        }
    }



    private val _courseList by lazy { MutableLiveData<ViewState<List<CourseEntry>>>() }
    val courseList: LiveData<ViewState<List<CourseEntry>>> get() = _courseList

    fun insertCourse(courseEntry: CourseEntry) {
        viewModelScope.launch {
            repository.saveCourse(courseEntry)
        }
    }

    fun getSaveCourseList() {
        viewModelScope.launch {
            repository.getCourseList()
                .map { recentList ->
                    ViewState.Success(recentList)
                }
                .catch {
                }
                .collect { result ->
                    _courseList.postValue(result)
                }
        }
    }

    fun deleteByCourseId(courseId : Int) {
        viewModelScope.launch {
            repository.deleteByCourseId(courseId)
        }
    }
}