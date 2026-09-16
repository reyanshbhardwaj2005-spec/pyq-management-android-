package com.example.iiitquestionbank.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.iiitquestionbank.R;
import com.example.iiitquestionbank.repository.PaperRepository;
import com.example.iiitquestionbank.utils.DownloadUtils;
import com.example.iiitquestionbank.utils.LoadingDialog;
import com.example.iiitquestionbank.utils.ToastUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultFragment extends Fragment {
    private TextView tvTitle;
    private TextView tvSemester;
    private TextView tvExamType;
    private TextView tvAcademicYear;
    private TextView tvBranch;
    private MaterialButton btnView;
    private MaterialButton btnDownload;
    private long paperId;

    public SearchResultFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvSemester = view.findViewById(R.id.tvSemester);
        tvExamType = view.findViewById(R.id.tvExamType);
        tvAcademicYear = view.findViewById(R.id.tvAcademicYear);
        tvBranch = view.findViewById(R.id.tvBranch);
        btnView = view.findViewById(R.id.btnView);
        btnDownload = view.findViewById(R.id.btnDownload);

        paperId = getArguments().getLong("id");

        if (getArguments() != null) {
            tvTitle.setText(getArguments().getString("title"));
            tvSemester.setText("Semester : " + getArguments().getInt("semester"));
            tvExamType.setText("Examination Type : " + getArguments().getString("examType"));
            tvAcademicYear.setText("Academic Year : " + getArguments().getInt("academicYear"));
            tvBranch.setText("Branch : " + getArguments().getString("Branch"));
        }

        btnView.setOnClickListener(v -> {
            downloadAndOpenPdf();
        });
        btnDownload.setOnClickListener(v -> {
            downloadPdf();
        });
    }

    private void downloadAndOpenPdf() {

        LoadingDialog loadingDialog = new LoadingDialog(requireContext());
        loadingDialog.show();
        PaperRepository repository = new PaperRepository();
        repository.downloadPaper(paperId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        loadingDialog.dismiss();

                        if(response.isSuccessful() && response.body()!=null){
                            try{
                                new Thread(() -> {
                                    try {
                                        File pdfFile = savePdf(response.body());

                                        requireActivity().runOnUiThread(() -> {
                                            openPdfViewer(pdfFile);
                                        });

                                    } catch (Exception e) {

                                        e.printStackTrace();

                                        requireActivity().runOnUiThread(() -> {
                                            ToastUtil.show(requireContext(), "Failed to save PDF");
                                        });

                                    }

                                }).start();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Error: " + e, Toast.LENGTH_LONG).show();
                            }
                        }

                        else{
                            ToastUtil.show(requireContext(), "Unable to open PDF");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        loadingDialog.dismiss();
                        ToastUtil.show(requireContext(), "Download Failed");
                    }
        });

    }

    private File savePdf(ResponseBody body) throws Exception {

        File file = new File(requireContext().getCacheDir(), "question_paper.pdf");
        InputStream inputStream = body.byteStream();
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int read;
        while((read = inputStream.read(buffer)) != -1){
            outputStream.write(buffer,0,read);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        return file;
    }

    private void openPdfViewer(File pdfFile){
        Bundle bundle = new Bundle();
        bundle.putString("pdfPath", pdfFile.getAbsolutePath());
        PdfViewerFragment fragment = new PdfViewerFragment();
        fragment.setArguments(bundle);
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();

    }

    private void downloadPdf() {

        LoadingDialog dialog = new LoadingDialog(requireContext());

        dialog.show();

        PaperRepository repository = new PaperRepository();

        repository.downloadPaper(paperId).enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            new Thread(() -> {

                                boolean success = DownloadUtils.savePdf(requireContext(), response.body(), "QuestionPaper_" + paperId + ".pdf");

                                requireActivity().runOnUiThread(() -> {

                                    dialog.dismiss();

                                    if (success) {
                                        ToastUtil.show(requireContext(), "Downloaded Successfully");
                                    } else {
                                        ToastUtil.show(requireContext(), "Download Failed");
                                    }
                                });
                            }).start();

                        } else {

                            dialog.dismiss();
                            ToastUtil.show(requireContext(), "Download Failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dialog.dismiss();
                        ToastUtil.show(requireContext(), "Network Error");
                    }
        });
    }
}
