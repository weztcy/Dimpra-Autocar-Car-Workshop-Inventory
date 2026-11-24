package com.workshop.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.workshop.config.AppConfig;
import com.workshop.dao.PartDao;
import com.workshop.dao.PurchaseOrderDao;
import com.workshop.dao.ServiceTransactionDao;
import com.workshop.dao.StockLedgerDao;
import com.workshop.model.*;
import com.workshop.util.CurrencyUtil;
import com.workshop.util.DateUtil;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

public class ReportService {
    private final ServiceTransactionDao trxDao = new ServiceTransactionDao();
    private final PurchaseOrderDao poDao = new PurchaseOrderDao();
    private final PartDao partDao = new PartDao();
    private final StockLedgerDao ledgerDao = new StockLedgerDao();

    // Fonts
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(24, 43, 73));
    private static final Font SUBTITLE_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(100, 116, 139));
    private static final Font SECTION_FONT = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(30, 41, 59));
    private static final Font TABLE_HEADER_FONT = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
    private static final Font TABLE_BODY_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(30, 41, 59));
    private static final Font TABLE_BOLD_FONT = new Font(Font.HELVETICA, 9, Font.BOLD, new Color(15, 23, 42));

    public boolean exportInvoicePdf(int transactionId, File targetPdf) {
        ServiceTransaction trx = trxDao.findById(transactionId);
        if (trx == null) return false;

        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(targetPdf));
            doc.open();

            // Header Banner
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{60, 40});

            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(Rectangle.NO_BORDER);
            cellLeft.addElement(new Paragraph(AppConfig.WORKSHOP_NAME, TITLE_FONT));
            cellLeft.addElement(new Paragraph(AppConfig.WORKSHOP_ADDRESS, SUBTITLE_FONT));
            cellLeft.addElement(new Paragraph("Telp: " + AppConfig.WORKSHOP_PHONE, SUBTITLE_FONT));
            cellLeft.addElement(new Paragraph("NPWP: " + AppConfig.WORKSHOP_TAX_ID, SUBTITLE_FONT));
            header.addCell(cellLeft);

            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(Rectangle.NO_BORDER);
            cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph invTitle = new Paragraph("FAKTUR SERVIS / INVOICE", new Font(Font.HELVETICA, 14, Font.BOLD, new Color(14, 116, 144)));
            invTitle.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(invTitle);
            Paragraph codeP = new Paragraph("No: " + trx.getTransactionCode(), TABLE_BOLD_FONT);
            codeP.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(codeP);
            Paragraph dateP = new Paragraph("Tanggal: " + DateUtil.formatDateTime(trx.getTransactionDate()), SUBTITLE_FONT);
            dateP.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(dateP);
            Paragraph statusP = new Paragraph("Status: " + trx.getPaymentStatus() + " (" + trx.getPaymentMethod() + ")", TABLE_BOLD_FONT);
            statusP.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(statusP);
            header.addCell(cellRight);

            doc.add(header);
            doc.add(new Paragraph(" "));

            // Customer & Vehicle Details Table
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{20, 30, 20, 30});

            addInfoRow(infoTable, "Pelanggan:", trx.getCustomerName(), "No. Polisi:", trx.getPlateNumber());
            addInfoRow(infoTable, "No. Telepon:", trx.getCustomerPhone(), "Kendaraan:", trx.getVehicleModel());
            addInfoRow(infoTable, "Jenis Servis:", trx.getServiceType(), "Odometer:", trx.getOdometerIn() + " KM");
            addInfoRow(infoTable, "Teknisi:", trx.getTechnicianName(), "Keluhan:", trx.getCustomerComplaint() != null ? trx.getCustomerComplaint() : "-");

            doc.add(infoTable);
            doc.add(new Paragraph(" "));

            // Parts Section
            doc.add(new Paragraph("1. PENGGANTIAN SUKU CADANG / SPARE PARTS", SECTION_FONT));
            PdfPTable partsTable = new PdfPTable(5);
            partsTable.setWidthPercentage(100);
            partsTable.setWidths(new float[]{15, 45, 12, 14, 14});
            addTableHeader(partsTable, new String[]{"Kode Part", "Nama Suku Cadang", "Qty", "Harga Satuan", "Subtotal"});

            for (ServicePartUsed pu : trx.getPartsUsed()) {
                partsTable.addCell(createCell(pu.getPartNumber(), Element.ALIGN_LEFT));
                partsTable.addCell(createCell(pu.getPartName() + " (" + pu.getBrand() + ")", Element.ALIGN_LEFT));
                partsTable.addCell(createCell(pu.getQuantity() + " " + pu.getUnitOfMeasure(), Element.ALIGN_CENTER));
                partsTable.addCell(createCell(CurrencyUtil.format(pu.getUnitPrice()), Element.ALIGN_RIGHT));
                partsTable.addCell(createCell(CurrencyUtil.format(pu.getSubtotal()), Element.ALIGN_RIGHT));
            }
            if (trx.getPartsUsed().isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("Tidak ada penggantian suku cadang", TABLE_BODY_FONT));
                empty.setColspan(5);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                partsTable.addCell(empty);
            }
            doc.add(partsTable);
            doc.add(new Paragraph(" "));

            // Labor Section
            doc.add(new Paragraph("2. JASA PERBAIKAN & PERAWATAN", SECTION_FONT));
            PdfPTable laborTable = new PdfPTable(4);
            laborTable.setWidthPercentage(100);
            laborTable.setWidths(new float[]{55, 15, 15, 15});
            addTableHeader(laborTable, new String[]{"Deskripsi Layanan / Pekerjaan", "Waktu (Jam)", "Tarif", "Subtotal"});

            for (ServiceLaborItem li : trx.getLaborItems()) {
                laborTable.addCell(createCell(li.getServiceName(), Element.ALIGN_LEFT));
                laborTable.addCell(createCell(li.getHours().toString() + " Jam", Element.ALIGN_CENTER));
                laborTable.addCell(createCell(CurrencyUtil.format(li.getRate()), Element.ALIGN_RIGHT));
                laborTable.addCell(createCell(CurrencyUtil.format(li.getSubtotal()), Element.ALIGN_RIGHT));
            }
            doc.add(laborTable);
            doc.add(new Paragraph(" "));

            // Totals Summary Table
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(50);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setWidths(new float[]{60, 40});

            addSummaryRow(totalTable, "Total Suku Cadang:", CurrencyUtil.format(trx.getPartsTotal()));
            addSummaryRow(totalTable, "Total Biaya Jasa:", CurrencyUtil.format(trx.getLaborTotal()));
            if (trx.getDiscount() != null && trx.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                addSummaryRow(totalTable, "Diskon:", "-" + CurrencyUtil.format(trx.getDiscount()));
            }
            addSummaryRow(totalTable, "PPN 11%:", CurrencyUtil.format(trx.getTaxAmount()));
            addSummaryRowGrand(totalTable, "GRAND TOTAL:", CurrencyUtil.format(trx.getGrandTotal()));

            doc.add(totalTable);
            doc.add(new Paragraph(" "));

            // Signatures
            PdfPTable sigTable = new PdfPTable(3);
            sigTable.setWidthPercentage(100);
            sigTable.setWidths(new float[]{33, 33, 34});

            addSignatureBox(sigTable, "Pelanggan", trx.getCustomerName());
            addSignatureBox(sigTable, "Teknisi Pemeriksa", trx.getTechnicianName());
            addSignatureBox(sigTable, "Service Advisor / Supervisor", AppConfig.WORKSHOP_NAME);

            doc.add(sigTable);
            doc.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportPurchaseOrderPdf(int poId, File targetPdf) {
        PurchaseOrder po = poDao.findById(poId);
        if (po == null) return false;

        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(targetPdf));
            doc.open();

            // Header Banner
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{60, 40});

            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(Rectangle.NO_BORDER);
            cellLeft.addElement(new Paragraph(AppConfig.WORKSHOP_NAME, TITLE_FONT));
            cellLeft.addElement(new Paragraph(AppConfig.WORKSHOP_ADDRESS, SUBTITLE_FONT));
            cellLeft.addElement(new Paragraph("Telp: " + AppConfig.WORKSHOP_PHONE, SUBTITLE_FONT));
            header.addCell(cellLeft);

            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(Rectangle.NO_BORDER);
            cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph poTitle = new Paragraph("PURCHASE ORDER (PO)", new Font(Font.HELVETICA, 14, Font.BOLD, new Color(37, 99, 235)));
            poTitle.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(poTitle);
            Paragraph codeP = new Paragraph("No: " + po.getPoNumber(), TABLE_BOLD_FONT);
            codeP.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(codeP);
            Paragraph dateP = new Paragraph("Tanggal: " + DateUtil.formatDate(po.getOrderDate()), SUBTITLE_FONT);
            dateP.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(dateP);
            Paragraph statusP = new Paragraph("Status: " + po.getStatus(), TABLE_BOLD_FONT);
            statusP.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(statusP);
            header.addCell(cellRight);

            doc.add(header);
            doc.add(new Paragraph(" "));

            // Supplier Details
            PdfPTable supTable = new PdfPTable(4);
            supTable.setWidthPercentage(100);
            supTable.setWidths(new float[]{20, 30, 20, 30});
            addInfoRow(supTable, "Supplier / Vendor:", po.getSupplierName(), "Tgl Estimasi:", DateUtil.formatDate(po.getExpectedDate()));
            addInfoRow(supTable, "Kontak Telepon:", po.getSupplierPhone() != null ? po.getSupplierPhone() : "-", "Tgl Diterima:", DateUtil.formatDate(po.getReceivedDate()));
            addInfoRow(supTable, "Status Bayar:", po.getPaymentStatus(), "Catatan:", po.getNotes() != null ? po.getNotes() : "-");
            doc.add(supTable);
            doc.add(new Paragraph(" "));

            // Items Table
            doc.add(new Paragraph("DAFTAR PENGADAAN BARANG / SUKU CADANG", SECTION_FONT));
            PdfPTable itemsTable = new PdfPTable(6);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{15, 35, 15, 10, 12, 13});
            addTableHeader(itemsTable, new String[]{"Kode Part", "Nama Barang", "Merek", "Satuan", "Harga Satuan", "Subtotal"});

            for (PurchaseOrderItem it : po.getItems()) {
                itemsTable.addCell(createCell(it.getPartNumber(), Element.ALIGN_LEFT));
                itemsTable.addCell(createCell(it.getPartName(), Element.ALIGN_LEFT));
                itemsTable.addCell(createCell(it.getBrand(), Element.ALIGN_LEFT));
                itemsTable.addCell(createCell(it.getQuantityOrdered() + " " + it.getUnitOfMeasure(), Element.ALIGN_CENTER));
                itemsTable.addCell(createCell(CurrencyUtil.format(it.getUnitCost()), Element.ALIGN_RIGHT));
                itemsTable.addCell(createCell(CurrencyUtil.format(it.getSubtotal()), Element.ALIGN_RIGHT));
            }
            doc.add(itemsTable);
            doc.add(new Paragraph(" "));

            // Total
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(40);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setWidths(new float[]{50, 50});
            addSummaryRowGrand(totalTable, "TOTAL PO:", CurrencyUtil.format(po.getTotalAmount()));
            doc.add(totalTable);

            doc.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportInventoryValuationPdf(File targetPdf) {
        List<Part> parts = partDao.findAll(null, null, null);
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(targetPdf));
            doc.open();

            // Header
            Paragraph title = new Paragraph("LAPORAN AUDIT & VALUASI NILAI ASET INVENTARIS SUKU CADANG", TITLE_FONT);
            Paragraph sub = new Paragraph(AppConfig.WORKSHOP_NAME + " - Per Tanggal Cetak: " + DateUtil.formatDate(new java.util.Date()), SUBTITLE_FONT);
            doc.add(title);
            doc.add(sub);
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{10, 25, 15, 12, 8, 10, 10, 10});
            addTableHeader(table, new String[]{"Kode Part", "Nama Suku Cadang", "Kategori", "Merek", "Lokasi", "Stok", "Harga Beli", "Total Valuasi"});

            BigDecimal grandTotalValuation = BigDecimal.ZERO;
            int totalStockQty = 0;

            for (Part p : parts) {
                BigDecimal rowVal = p.getStockValuation();
                grandTotalValuation = grandTotalValuation.add(rowVal);
                totalStockQty += p.getCurrentStock();

                table.addCell(createCell(p.getPartNumber(), Element.ALIGN_LEFT));
                table.addCell(createCell(p.getPartName(), Element.ALIGN_LEFT));
                table.addCell(createCell(p.getCategoryName(), Element.ALIGN_LEFT));
                table.addCell(createCell(p.getBrand(), Element.ALIGN_LEFT));
                table.addCell(createCell(p.getStorageBin(), Element.ALIGN_CENTER));
                table.addCell(createCell(String.valueOf(p.getCurrentStock()), Element.ALIGN_CENTER));
                table.addCell(createCell(CurrencyUtil.format(p.getCostPrice()), Element.ALIGN_RIGHT));
                table.addCell(createCell(CurrencyUtil.format(rowVal), Element.ALIGN_RIGHT));
            }
            doc.add(table);
            doc.add(new Paragraph(" "));

            // Grand Total Summary
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(45);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setWidths(new float[]{55, 45});
            addSummaryRow(totalTable, "Total Jenis Suku Cadang:", parts.size() + " Item");
            addSummaryRow(totalTable, "Total Kuantitas Fisik:", totalStockQty + " Unit");
            addSummaryRowGrand(totalTable, "NILAI TOTAL ASET INVENTARIS:", CurrencyUtil.format(grandTotalValuation));
            doc.add(totalTable);

            doc.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportStockCardPdf(int partId, File targetPdf) {
        Part p = partDao.findById(partId);
        if (p == null) return false;
        List<StockLedger> ledger = ledgerDao.findByPartId(partId);

        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(targetPdf));
            doc.open();

            Paragraph title = new Paragraph("KARTU STOK AUDIT / INVENTORY STOCK CARD", TITLE_FONT);
            Paragraph sub = new Paragraph(String.format("Part: %s - %s | Merek: %s | Bin: %s | Stok Saat Ini: %d %s",
                    p.getPartNumber(), p.getPartName(), p.getBrand(), p.getStorageBin(), p.getCurrentStock(), p.getUnitOfMeasure()), SUBTITLE_FONT);
            doc.add(title);
            doc.add(sub);
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{16, 14, 14, 10, 10, 10, 14, 12});
            addTableHeader(table, new String[]{"Waktu Transaksi", "Jenis Mutasi", "No. Referensi", "Mutasi (+/-)", "Sebelum", "Sesudah", "Harga Satuan", "Oleh Petugas"});

            for (StockLedger sl : ledger) {
                table.addCell(createCell(DateUtil.formatDateTime(sl.getTransactionTime()), Element.ALIGN_CENTER));
                table.addCell(createCell(sl.getTransactionType(), Element.ALIGN_CENTER));
                table.addCell(createCell(sl.getReferenceNo(), Element.ALIGN_LEFT));
                table.addCell(createCell((sl.getQuantityChange() > 0 ? "+" : "") + sl.getQuantityChange(), Element.ALIGN_CENTER));
                table.addCell(createCell(String.valueOf(sl.getStockBefore()), Element.ALIGN_CENTER));
                table.addCell(createCell(String.valueOf(sl.getStockAfter()), Element.ALIGN_CENTER));
                table.addCell(createCell(CurrencyUtil.format(sl.getUnitCost()), Element.ALIGN_RIGHT));
                table.addCell(createCell(sl.getCreatedByName() != null ? sl.getCreatedByName() : "-", Element.ALIGN_LEFT));
            }
            doc.add(table);
            doc.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exportTableToCsv(JTable table, File file) {
        try (PrintWriter pw = new PrintWriter(file, "UTF-8")) {
            TableModel model = table.getModel();
            int colCount = model.getColumnCount();

            // Header
            for (int i = 0; i < colCount; i++) {
                pw.print("\"" + model.getColumnName(i) + "\"");
                if (i < colCount - 1) pw.print(",");
            }
            pw.println();

            // Rows
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < colCount; c++) {
                    Object val = model.getValueAt(r, c);
                    String s = (val != null) ? val.toString().replace("\"", "\"\"") : "";
                    pw.print("\"" + s + "\"");
                    if (c < colCount - 1) pw.print(",");
                }
                pw.println();
            }
            JOptionPane.showMessageDialog(null, "Data berhasil diexport ke CSV:\n" + file.getAbsolutePath(), "Export Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mengexport CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper methods for PDF formatting
    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, TABLE_HEADER_FONT));
            c.setBackgroundColor(new Color(30, 41, 59));
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setPadding(6);
            table.addCell(c);
        }
    }

    private PdfPCell createCell(String text, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text != null ? text : "", TABLE_BODY_FONT));
        c.setHorizontalAlignment(align);
        c.setPadding(5);
        return c;
    }

    private void addInfoRow(PdfPTable table, String l1, String v1, String l2, String v2) {
        PdfPCell c1 = new PdfPCell(new Phrase(l1, TABLE_BOLD_FONT));
        c1.setBorder(Rectangle.NO_BORDER);
        PdfPCell c2 = new PdfPCell(new Phrase(v1, TABLE_BODY_FONT));
        c2.setBorder(Rectangle.NO_BORDER);
        PdfPCell c3 = new PdfPCell(new Phrase(l2, TABLE_BOLD_FONT));
        c3.setBorder(Rectangle.NO_BORDER);
        PdfPCell c4 = new PdfPCell(new Phrase(v2, TABLE_BODY_FONT));
        c4.setBorder(Rectangle.NO_BORDER);

        table.addCell(c1);
        table.addCell(c2);
        table.addCell(c3);
        table.addCell(c4);
    }

    private void addSummaryRow(PdfPTable table, String label, String val) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, TABLE_BODY_FONT));
        c1.setBorder(Rectangle.NO_BORDER);
        PdfPCell c2 = new PdfPCell(new Phrase(val, TABLE_BOLD_FONT));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c1);
        table.addCell(c2);
    }

    private void addSummaryRowGrand(PdfPTable table, String label, String val) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, SECTION_FONT));
        c1.setBackgroundColor(new Color(241, 245, 249));
        c1.setPadding(6);
        PdfPCell c2 = new PdfPCell(new Phrase(val, SECTION_FONT));
        c2.setBackgroundColor(new Color(241, 245, 249));
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setPadding(6);
        table.addCell(c1);
        table.addCell(c2);
    }

    private void addSignatureBox(PdfPTable table, String title, String name) {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.addElement(new Paragraph(title, TABLE_BOLD_FONT));
        c.addElement(new Paragraph("\n\n\n"));
        Paragraph np = new Paragraph("(" + name + ")", TABLE_BODY_FONT);
        np.setAlignment(Element.ALIGN_CENTER);
        c.addElement(np);
        table.addCell(c);
    }
}
