package com.example.iiitquestionbank.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iiitquestionbank.R;
import com.example.iiitquestionbank.adapter.PaperAdapter;
import com.example.iiitquestionbank.model.StudentPaperResponse;
import com.example.iiitquestionbank.repository.PaperRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsFragment extends Fragment {

    private RecyclerView recyclerViewPapers;
    private ProgressBar progressBar;
    private List<StudentPaperResponse> paperList;
    private PaperRepository repository;
    private TextView txtResultCount;
    private MaterialButton btnSearchAgain;
    private MaterialToolbar toolbar;

    public SearchResultsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        recyclerViewPapers = view.findViewById(R.id.recyclerViewPapers);
        progressBar = view.findViewById(R.id.progressBar);
        repository = new PaperRepository();
        recyclerViewPapers.setLayoutManager(new LinearLayoutManager(requireContext()));
        txtResultCount = view.findViewById(R.id.txtResultCount);
        btnSearchAgain = view.findViewById(R.id.btnSearchAgain);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack());

        if (getArguments() != null) {
            paperList = (ArrayList<StudentPaperResponse>) getArguments().getSerializable("papers");
            txtResultCount.setText("Found " + paperList.size() + " papers");

            PaperAdapter adapter = new PaperAdapter(paperList, new PaperAdapter.OnPaperClickListener() {
                        @Override
                        public void onPaperClick(StudentPaperResponse paper) {
                            openPdf(paper);
                        }
                        @Override
                        public void onDownloadClick(StudentPaperResponse paper) {
                            downloadPdf(paper);
                        }
                    }
            );
            recyclerViewPapers.setAdapter(adapter);
            recyclerViewPapers.scheduleLayoutAnimation();
        }
        btnSearchAgain.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        return view;
    }

    private void openPdf(StudentPaperResponse paper) {

        progressBar.setVisibility(View.VISIBLE);

        repository.downloadPaper(paper.getId()).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Unable to open PDF", Toast.LENGTH_SHORT).show();

                    return;
                }

                new Thread(() -> {

                    try {
                        File pdfFile = new File(requireContext().getCacheDir(), paper.getId() + ".pdf");

                        InputStream input = response.body().byteStream();
                        FileOutputStream output = new FileOutputStream(pdfFile);

                        byte[] buffer = new byte[4096];
                        int count;

                        while ((count = input.read(buffer)) != -1) {
                            output.write(buffer, 0, count);
                        }

                        output.flush();
                        output.close();
                        input.close();

                        requireActivity().runOnUiThread(() -> {

                            progressBar.setVisibility(View.GONE);

                            Bundle bundle = new Bundle();
                            bundle.putString("pdfPath", pdfFile.getAbsolutePath());

                            bundle.putString("title", paper.getTitle());
                            toolbar.setTitle(getArguments().getString("title"));

                            PdfViewerFragment fragment = new PdfViewerFragment();
                            fragment.setArguments(bundle);
                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainer, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        });
                    } catch (Exception e) {

                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Unable to open PDF", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadPdf(StudentPaperResponse paper) {

        repository.downloadPaper(paper.getId()).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Download failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                new Thread(() -> {

                    try {

                        File downloadDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS);

                        if (!downloadDir.exists()) {
                            downloadDir.mkdirs();
                        }

                        String fileName = paper.getTitle().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

                        File pdfFile = new File(downloadDir, fileName);

                        InputStream inputStream = response.body().byteStream();
                        OutputStream outputStream = new FileOutputStream(pdfFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();

                        requireActivity().runOnUiThread(() -> {

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(requireContext(), "Downloaded to Downloads folder", Toast.LENGTH_LONG).show();

                        });

                    } catch (Exception e) {

                        e.printStackTrace();

                        requireActivity().runOnUiThread(() -> {

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(requireContext(), "Download failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}