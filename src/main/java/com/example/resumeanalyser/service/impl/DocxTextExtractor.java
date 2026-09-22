package com.example.resumeanalyser.service.impl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class DocxTextExtractor implements FileTextExtractor {

    @Override
    public boolean supports(String extension) {
        return "docx".equalsIgnoreCase(extension);
    }

    @Override
    public String extract(InputStream in) throws IOException {
        try (XWPFDocument document = new XWPFDocument(in)) {
            StringBuilder text = new StringBuilder();

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append(System.lineSeparator());
            }

            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        text.append(cell.getText()).append(' ');
                    }
                    text.append(System.lineSeparator());
                }
            }

            return text.toString();
        }
    }
}
