package com.example.iiitquestionbank.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.iiitquestionbank.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.appbar.MaterialToolbar;
import java.io.File;

public class PdfViewerFragment extends Fragment {

    private PDFView pdfView;
    private TextView tvPage;

    public PdfViewerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);

        pdfView = view.findViewById(R.id.pdfView);
        tvPage = view.findViewById(R.id.tvPage);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().onBackPressed());

        if (getArguments() != null) {

            String path = getArguments().getString("pdfPath");

            if (path != null) {

                File pdfFile = new File(path);

                if (pdfFile.exists()) {

                    pdfView.fromFile(pdfFile)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .defaultPage(0)
                            .spacing(8)
                            .autoSpacing(true)
                            .pageSnap(true)
                            .pageFling(true)

                            .onLoad(nbPages -> {
                                tvPage.setText("Page 1 / " + nbPages);
                            })

                            .onPageChange((page, pageCount) -> {
                                tvPage.setText("Page " + (page + 1) + " / " + pageCount);
                            })

                            .load();
                } else {
                    Toast.makeText(requireContext(), "PDF not found", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return view;
    }
}
