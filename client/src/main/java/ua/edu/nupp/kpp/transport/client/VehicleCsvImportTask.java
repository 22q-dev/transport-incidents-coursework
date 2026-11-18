package ua.edu.nupp.kpp.transport.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class VehicleCsvImportTask extends Task<Integer> {
    private static final Logger LOG = Logger.getLogger(VehicleCsvImportTask.class.getName());
    private static final String EXPECTED_HEADER = "route_id;board_number;model_name;capacity";

    private final Path csvPath;
    private final TransportApiClient apiClient;

    public VehicleCsvImportTask(Path csvPath, TransportApiClient apiClient) {
        this.csvPath = csvPath;
        this.apiClient = apiClient;
    }

    @Override
    protected Integer call() throws Exception {
        LOG.info("Початок фонового CSV-імпорту транспортних засобів із файлу: " + csvPath);
        if (!Files.exists(csvPath)) {
            throw new IllegalArgumentException("CSV-файл не знайдено: " + csvPath);
        }
        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV-файл порожній");
        }
        String header = lines.get(0).trim().replace("\uFEFF", "");
        if (!EXPECTED_HEADER.equalsIgnoreCase(header)) {
            throw new IllegalArgumentException("Некоректний заголовок CSV: " + header);
        }

        int totalRows = lines.size() - 1;
        int importedCount = 0;

        for (int i = 1; i < lines.size(); i++) {
            String row = lines.get(i).trim();
            if (row.isEmpty()) {
                updateProgress(i, totalRows);
                continue;
            }
            String[] parts = row.split(";");
            if (parts.length != 4) {
                LOG.warning("Пропущено рядок №" + (i + 1) + " через неправильну кількість полів: " + row);
                updateProgress(i, totalRows);
                continue;
            }
            int routeId = Integer.parseInt(parts[0].trim());
            String boardNumber = parts[1].trim();
            String modelName = parts[2].trim();
            int capacity = Integer.parseInt(parts[3].trim());

            Vehicle vehicle = new Vehicle(0, routeId, boardNumber, modelName, capacity);
            apiClient.addVehicle(vehicle);
            importedCount++;

            updateProgress(i, totalRows);
            updateMessage("Опрацьовано рядок " + i + " із " + totalRows + " (" + boardNumber + ")");
            Thread.sleep(180);
        }

        updateMessage("Готово. Імпортовано записів: " + importedCount);
        LOG.info("Фоновий CSV-імпорт успішно завершено. Додано записів: " + importedCount);
        return importedCount;
    }
}
