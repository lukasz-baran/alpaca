package com.evolve.gui;

import com.evolve.content.ContentStoreService;
import com.evolve.gui.events.StageReadyEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;
    private final ContentStoreService contentStoreService;
    private final Resource resource;

    public PrimaryStageInitializer(
            FxWeaver fxWeaver,
            ContentStoreService contentStoreService,
            @Value("classpath:alpaca.png") Resource resource) {
        this.fxWeaver = fxWeaver;
        this.contentStoreService = contentStoreService;
        this.resource = resource;
    }


    @SneakyThrows
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        log.info("onApplicationEvent {}", event);
        // store
//        final File alpaca = resource.getFile();
//        ContentFile contentFile = contentStoreService.setContent(alpaca);
//        Long savedId = contentFile.getId();

        // read
//        log.info("reading file with id: {}", savedId);
//        var is = contentStoreService.getContent(savedId);
//        byte[] bytes = ByteStreams.toByteArray(is);
//        FileUtils.writeByteArrayToFile(new File("zapis123.png"), bytes);


//        Stage stage = event.stage;
//        var node = fxWeaver.loadView(MainController.class);
//        Scene scene = new Scene(fxWeaver.loadView(MainController.class), 400, 300);
//        stage.setScene(scene);
//        stage.show();
    }
}