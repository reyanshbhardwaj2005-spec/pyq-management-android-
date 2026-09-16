package com.example.iiitquestionbank.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.iiitquestionbank.R;
import com.example.iiitquestionbank.model.ApiResponse;
import com.example.iiitquestionbank.model.Branches;
import com.example.iiitquestionbank.model.ExaminationType;
import com.example.iiitquestionbank.model.StudentPaperResponse;
import com.example.iiitquestionbank.repository.PaperRepository;
import com.example.iiitquestionbank.utils.NetworkUtil;
import com.example.iiitquestionbank.utils.ProgressUtil;
import com.example.iiitquestionbank.utils.ToastUtil;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private AutoCompleteTextView  spinnerSemester;
    private AutoCompleteTextView  spinnerExamType;
    private AutoCompleteTextView  spinnerAcademicYear;
    private AutoCompleteTextView spinnerBranch;
    private MaterialButton btnSearch;
    private ProgressBar progressBar;
    private PaperRepository repository;
    private Integer selectedSemester;
    private Integer selectedAcademicYear;
    private String selectedBranch;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        spinnerSemester = view.findViewById(R.id.spinnerSemester);
        spinnerExamType = view.findViewById(R.id.spinnerExamType);
        spinnerAcademicYear = view.findViewById(R.id.spinnerAcademicYear);
        spinnerBranch = view.findViewById(R.id.spinnerBranch);
        btnSearch = view.findViewById(R.id.btnSearch);
        progressBar = view.findViewById(R.id.progressBar);
        repository = new PaperRepository();

        loadSemesters();
        loadExaminationTypes();
        loadAcademicYears();
        loadBranches();

        btnSearch.setOnClickListener(v -> searchPaper());

        return view;
    }

    private void loadSemesters() {
        progressBar.setVisibility(View.VISIBLE);
        repository.getSemesters().enqueue(new Callback<ApiResponse<List<Integer>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {

                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful() && response.body()!=null){

                    List<String> semesterList = new java.util.ArrayList<>();
                    semesterList.add("All");

                    for (Integer semester : response.body().getData()) {
                        semesterList.add(String.valueOf(semester));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, semesterList);

                    spinnerSemester.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ToastUtil.show(requireContext(), t.getMessage());
            }
        });
    }

    private void loadExaminationTypes() {
        repository.getExaminationTypes().enqueue(new Callback<ApiResponse<List<ExaminationType>>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<List<ExaminationType>>> call, Response<ApiResponse<List<ExaminationType>>> response) {

                        if(response.isSuccessful() && response.body()!=null){
                            List<String> examList = new java.util.ArrayList<>();

                            examList.add("All");

                            for (ExaminationType exam : response.body().getData()) {
                                if (exam != null) {
                                    examList.add(exam.name());
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, examList);
                            spinnerExamType.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<ExaminationType>>> call, Throwable t) {
                        ToastUtil.show(requireContext(), t.getMessage());
                    }
        });
    }

    private void loadAcademicYears() {
        repository.getAcademicYears().enqueue(new Callback<ApiResponse<List<Integer>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {

                        if(response.isSuccessful() && response.body()!=null){
                            List<String> yearList = new java.util.ArrayList<>();
                            yearList.add("All");

                            for (Integer year : response.body().getData()) {
                                yearList.add(String.valueOf(year));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, yearList);
                            spinnerAcademicYear.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                        ToastUtil.show(requireContext(), t.getMessage());
                    }
        });
    }

    private void loadBranches() {
        repository.getBranches().enqueue(new Callback<ApiResponse<List<Branches>>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<List<Branches>>> call, Response<ApiResponse<List<Branches>>> response) {

                        if(response.isSuccessful() && response.body()!=null){
                            List<String> branchList = new ArrayList<>();
                            branchList.add("All");

                            for (Branches branch : response.body().getData()) {
                                branchList.add(branch.name());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, branchList);
                            spinnerBranch.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Branches>>> call, Throwable t) {
                        ToastUtil.show(requireContext(), t.getMessage());
                    }
        });
    }

    private void searchPaper() {

        if (!NetworkUtil.isInternetAvailable(requireContext())) {
            ToastUtil.show(requireContext(), "No Internet Connection");
            return;
        }


            if (spinnerSemester == null || spinnerExamType == null || spinnerAcademicYear == null || spinnerBranch == null) {
                ToastUtil.show(requireContext(), "Screen is not initialized.");
                return;
            }

            if (spinnerSemester.getAdapter() == null ||
                    spinnerExamType.getAdapter() == null ||
                    spinnerAcademicYear.getAdapter() == null ||
                    spinnerBranch.getAdapter() == null) {

                ToastUtil.show(requireContext(), "Please wait, filters are loading...");
                return;
            }

        if (spinnerSemester.getText().toString().trim().isEmpty()) {
            ToastUtil.show(requireContext(), "Please select a semester");
            return;
        }

        if (spinnerExamType.getText().toString().trim().isEmpty()) {
            ToastUtil.show(requireContext(), "Please select an examination type");
            return;
        }

        if (spinnerAcademicYear.getText().toString().trim().isEmpty()) {
            ToastUtil.show(requireContext(), "Please select an academic year");
            return;
        }

        if (spinnerBranch.getText().toString().trim().isEmpty()) {
            ToastUtil.show(requireContext(), "Please select a branch");
            return;
        }

        String semester = spinnerSemester.getText().toString().trim();
        if (semester.equals("All")) {
            selectedSemester = null;
        } else {
            selectedSemester = Integer.parseInt(semester);
        }

        String examType = spinnerExamType.getText().toString().trim();
        String selectedExamTypeString;
        if (examType.equals("All")) {
            selectedExamTypeString = null;
        } else {
            selectedExamTypeString = examType;
        }

        String year = spinnerAcademicYear.getText().toString().trim();
        if (year.equals("All")) {
            selectedAcademicYear = null;
        } else {
            selectedAcademicYear = Integer.parseInt(year);
        }

        selectedBranch = spinnerBranch.getText().toString().trim();
        if (selectedBranch.equals("All")) {
            selectedBranch = null;
        }

        ProgressUtil.show(progressBar);

        repository.searchPaper(selectedSemester, selectedExamTypeString, selectedAcademicYear, selectedBranch).enqueue(new Callback<ApiResponse<List<StudentPaperResponse>>>() {

            @Override
            public void onResponse(Call<ApiResponse<List<StudentPaperResponse>>> call, Response<ApiResponse<List<StudentPaperResponse>>> response) {

                ProgressUtil.hide(progressBar);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    List<StudentPaperResponse> papers = response.body().getData();
                    if (papers.isEmpty()) {
                        ToastUtil.show(requireContext(), "No Question Papers Found");
                        return;
                    }
                    openResultFragment(papers);
                } else {
                    ToastUtil.show(requireContext(), "No Question Paper Found");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StudentPaperResponse>>> call, Throwable t) {

                ProgressUtil.hide(progressBar);
                ToastUtil.show(requireContext(), "Unable to connect to server");
            }
        });
    }


    private void openResultFragment(List<StudentPaperResponse> papers) {

        Bundle bundle = new Bundle();

        bundle.putSerializable("papers", new java.util.ArrayList<>(papers));

        SearchResultsFragment fragment = new SearchResultsFragment();

        fragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right)
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
