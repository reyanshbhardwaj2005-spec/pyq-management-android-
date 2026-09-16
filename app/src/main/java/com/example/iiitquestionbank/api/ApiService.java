package com.example.iiitquestionbank.api;

import com.example.iiitquestionbank.model.ApiResponse;
import com.example.iiitquestionbank.model.Branches;
import com.example.iiitquestionbank.model.ExaminationType;
import com.example.iiitquestionbank.model.StudentPaperResponse;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface ApiService {

    @GET("api/public/papers/search")
    Call<ApiResponse<List<StudentPaperResponse>>> searchPaper(
            @Query("semester") Integer semester,
            @Query("examinationType") String examinationType,
            @Query("academicYear") Integer academicYear,
            @Query("branch") String branch);

    @Streaming
    @GET("api/public/papers/{id}/download")
    Call<ResponseBody> downloadPaper(@Path("id") Long id);

    @GET("api/public/papers/filters/academic-years")
    Call<ApiResponse<List<Integer>>> getAcademicYears();

    @GET("api/public/papers/filters/branches")
    Call<ApiResponse<List<Branches>>> getBranches();

    @GET("api/public/papers/filters/examination-types")
    Call<ApiResponse<List<ExaminationType>>> getExaminationTypes();

    @GET("api/public/papers/filters/semesters")
    Call<ApiResponse<List<Integer>>> getSemesters();

}