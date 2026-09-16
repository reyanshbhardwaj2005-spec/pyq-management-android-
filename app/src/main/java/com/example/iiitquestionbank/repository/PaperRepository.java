package com.example.iiitquestionbank.repository;

import com.example.iiitquestionbank.api.ApiService;
import com.example.iiitquestionbank.api.RetrofitClient;
import com.example.iiitquestionbank.model.ApiResponse;
import com.example.iiitquestionbank.model.Branches;
import com.example.iiitquestionbank.model.ExaminationType;
import com.example.iiitquestionbank.model.StudentPaperResponse;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
public class PaperRepository {
    private final ApiService apiService;
    public PaperRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public Call<ApiResponse<List<Integer>>> getAcademicYears() {
        return apiService.getAcademicYears();
    }

    public Call<ApiResponse<List<Branches>>> getBranches() {
        return apiService.getBranches();
    }

    public Call<ApiResponse<List<ExaminationType>>> getExaminationTypes() {
        return apiService.getExaminationTypes();
    }

    public Call<ApiResponse<List<Integer>>> getSemesters() {
        return apiService.getSemesters();
    }

    public Call<ApiResponse<List<StudentPaperResponse>>> searchPaper(Integer semester, String examinationType, Integer academicYear, String branch){
        return apiService.searchPaper(semester, examinationType, academicYear, branch);
    }

    public Call<ResponseBody> downloadPaper(Long id) {
        return apiService.downloadPaper(id);
    }
}