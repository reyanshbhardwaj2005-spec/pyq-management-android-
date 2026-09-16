package com.example.iiitquestionbank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iiitquestionbank.R;
import com.example.iiitquestionbank.model.StudentPaperResponse;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.iiitquestionbank.repository.PaperRepository;
import com.example.iiitquestionbank.utils.PdfThumbnailUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.PaperViewHolder> {

    private final List<StudentPaperResponse> paperList;
    private final OnPaperClickListener listener;
    private final PaperRepository repository = new PaperRepository();

    public interface OnPaperClickListener {
        void onPaperClick(StudentPaperResponse paper);
        void onDownloadClick(StudentPaperResponse paper);
    }

    public PaperAdapter(List<StudentPaperResponse> paperList, OnPaperClickListener listener) {
        this.paperList = paperList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paper, parent, false);
        return new PaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {

        StudentPaperResponse paper = paperList.get(position);

        holder.txtTitle.setText(paper.getTitle());
        holder.txtSemester.setText("Semester : " + paper.getSemester());
        holder.txtExamType.setText("Exam : " + paper.getExaminationType());
        holder.txtAcademicYear.setText("Academic Year : " + paper.getAcademicYear());
        holder.txtBranch.setText("Branch : " + paper.getBranch());

        loadPdfPreview(holder.itemView.getContext(), paper.getId(), holder.imgPreview);

        holder.itemView.setOnClickListener(v -> {

            v.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(80)
                    .withEndAction(() -> {

                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(80)
                                .start();

                        listener.onPaperClick(paper);

                    })
                    .start();

        });

        holder.btnDownload.setOnClickListener(v ->
                listener.onDownloadClick(paper));
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    static class PaperViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtSemester;
        TextView txtExamType;
        TextView txtAcademicYear;
        TextView txtBranch;
        ImageView imgPreview;
        ImageButton btnDownload;
        public PaperViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSemester = itemView.findViewById(R.id.txtSemester);
            txtExamType = itemView.findViewById(R.id.txtExamType);
            txtAcademicYear = itemView.findViewById(R.id.txtAcademicYear);
            txtBranch = itemView.findViewById(R.id.txtBranch);
            imgPreview = itemView.findViewById(R.id.imgPreview);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }

    private void loadPdfPreview(Context context, Long paperId, ImageView imageView) {

        imageView.setImageResource(R.drawable.ic_pdf);

        File pdfFile = new File(context.getCacheDir(), paperId + ".pdf");

        // Already downloaded
        if (pdfFile.exists()) {

            Bitmap bitmap = PdfThumbnailUtil.getThumbnail(pdfFile);

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            return;
        }

        repository.downloadPaper(paperId).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                new Thread(() -> {

                    try {

                        InputStream input = response.body().byteStream();
                        OutputStream output = new FileOutputStream(pdfFile);

                        byte[] buffer = new byte[4096];
                        int count;

                        while ((count = input.read(buffer)) != -1) {
                            output.write(buffer, 0, count);
                        }

                        output.flush();
                        output.close();
                        input.close();

                        Bitmap bitmap = PdfThumbnailUtil.getThumbnail(pdfFile);

                        if (bitmap != null) {
                            imageView.post(() -> {

                                imageView.setAlpha(0f);

                                imageView.setImageBitmap(bitmap);

                                imageView.animate()
                                        .alpha(1f)
                                        .setDuration(300)
                                        .start();

                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }).start();
            }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
        });

    }
}