package io.abbadingo;

import dataset.Dataset;
import dataset.Sequence;
import io.file.FileService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbbadingoReaderService {

    private FileService fileService = FileService.getInstance();

    private static AbbadingoReaderService instance;

    private AbbadingoReaderService() {
    }

    public static AbbadingoReaderService getInstance() {
        if (instance == null)
            instance = new AbbadingoReaderService();
        return instance;
    }

    public Dataset loadFile(String path) {
        return new Dataset(parseSentences(fileService.readFileToStream(path)));
    }

    private List<Sequence> parseSentences(Stream<String> stream) {
        return stream.map(line -> {
            Sequence result = new Sequence();
            String[] tokens = line.split(" ");
            if (tokens.length > 2) {
                result.setPositive(Integer.parseInt(tokens[0]) == 1);
                result.setValue(String.join(Sequence.WORD_SEPARATOR, Arrays.copyOfRange(tokens, 2, tokens.length)));
            }
            return result;
        }).collect(Collectors.toList());
    }

}
