package org.example.facebookapp.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.facebookapp.model.Message;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {
    private final MessageService messageService;

    public void export(HttpServletResponse response, List<Map<String, Long>> allMaps) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        response.setCharacterEncoding("UTF-8");
        document.open();

        Font fontHeader = FontFactory.getFont("fonts/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 22);
        Paragraph headerParagraph = new Paragraph("Wyeksportowane Dane", fontHeader);
        headerParagraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(headerParagraph);

        Font fontSubHeader = FontFactory.getFont("fonts/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
        Paragraph dateParagraph = new Paragraph(
                "Wygenerowane: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                fontSubHeader
        );
        dateParagraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(dateParagraph);

        String[] titles = {
                "Wiadomości według uczestnika",
                "Wiadomości według dnia tygodnia",
                "Wiadomości według dnia miesiąca",
                "Wiadomości według roku",
                "Wiadomości według miesiąca",
                "Użycie emotikon"
        };

        for (int i = 0; i < allMaps.size(); i++) {
            Map<String, Long> dataMap = allMaps.get(i);

            Font fontSection = FontFactory.getFont("fonts/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16);
            Paragraph sectionParagraph = new Paragraph(titles[i], fontSection);
            sectionParagraph.setSpacingBefore(20f);
            document.add(sectionParagraph);

            if (dataMap != null && !dataMap.isEmpty()) {
                Font fontContent = FontFactory.getFont("fonts/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
                Paragraph contentParagraph = new Paragraph();
                contentParagraph.setFont(fontContent);

                for (Map.Entry<String, Long> entry : dataMap.entrySet()) {
                    contentParagraph.add(entry.getKey() + ": " + entry.getValue() + "\n");
                }
                document.add(contentParagraph);
            } else {
                Font fontNoData = FontFactory.getFont(FontFactory.TIMES_ITALIC);
                document.add(new Paragraph("Brak dostępnych danych", fontNoData));
            }
        }
        document.close();
    }

    public void createPDF(HttpServletResponse response, File file, String fileName) throws IOException {
        response.setContentType("application/pdf");
        String shortName = fileName.substring(0, fileName.lastIndexOf('.'));
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + shortName + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Map<String, Long>> allMaps = getAllMaps(file);
        export(response, allMaps);
    }

    private List<Map<String, Long>> getAllMaps(File file) throws IOException {
        List<Message> messageList = messageService.getMessageFromFile(file);
        List<Map<String, Long>> allMaps = new ArrayList<>();

        allMaps.add(messageService.getParticipantsMessageCount(file));
        allMaps.add(messageService.getDayOfTheWeekCount(messageList));
        allMaps.add(messageService.getDayOfTheMonthCount(messageList));
        allMaps.add(messageService.getYearCount(messageList));
        allMaps.add(messageService.getMonthCount(messageList));
        allMaps.add(messageService.getEmojiCount(messageList));

        return allMaps;
    }
}





