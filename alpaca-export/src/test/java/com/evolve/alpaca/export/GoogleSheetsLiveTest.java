package com.evolve.alpaca.export;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GoogleSheetsLiveTest {

    private static Sheets sheetsService;
    //private static String SPREADSHEET_ID = "1sILuxZUnyl_7-MlNThjt765oWshN3Xs-PPLfqYe4DhI";
    private static String SPREADSHEET_ID = "1av-YydkA3whcjxRk4Daocl-cUCM0GvWPMwPCXxZP0Pk";


    @BeforeAll
    public static void setup() throws GeneralSecurityException, IOException {
        sheetsService = SheetsServiceUtil.getSheetsService();
    }

    @Test
    public void whenWriteSheet_thenReadSheetOk() throws IOException {
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList("Expenses January"),
                        Arrays.asList("books", "30"),
                        Arrays.asList("pens", "10"),
                        Arrays.asList("Expenses February"),
                        Arrays.asList("clothes", "20"),
                        Arrays.asList("shoes", "5")));

        BatchUpdateSpreadsheetRequest request = new BatchUpdateSpreadsheetRequest();
        request.setRequests(List.of(new Request()));

        sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID,
                request).execute();
        List<Request> requests = new ArrayList<>();
//        requests.add(new Request().setInsertInlineImage(new InsertInlineImageRequest()
//                .setUri("https://fonts.gstatic.com/s/i/productlogos/docs_2020q4/v6/web-64dp/logo_docs_2020q4_color_1x_web_64dp.png")
//                .setLocation(new Location().setIndex(1))
//                .setObjectSize(new Size()
//                        .setHeight(new Dimension()
//                                .setMagnitude(50.0)
//                                .setUnit("PT"))
//                        .setWidth(new Dimension()
//                                .setMagnitude(50.0)
//                                .setUnit("PT")))));

        //BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests)


        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "A1", body)
                .setValueInputOption("RAW")
                .execute();
    }

    @Test
    public void test() throws IOException {
        Spreadsheet spreadSheet = new Spreadsheet().setProperties(
                new SpreadsheetProperties().setTitle("alpaca-test"));
        Spreadsheet result = sheetsService
                .spreadsheets()
                .create(spreadSheet).execute();

        assertThat(result.getSpreadsheetId())
                .isNotNull();
    }

}
