package daemon.Backup.Compression;

import daemon.Config.CompressionType;
import logger.LoggerUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

public class CompressorFactory {
    private static final Logger logger = LoggerUtil.getLogger(CompressorFactory.class);

    public static Compressor getCompressor(CompressionType compressionType, File input, File output) {
        switch (compressionType) {
            case NONE -> {
                logger.info("Creating NoCompressor");
                return new NoCompressor(input, output);
            }
            case ZIP -> {
                logger.info("Creating ZipCompressor");
                return new ZipCompressor(input, output);
            }
            case GZIP -> {
                logger.info("Creating GzipCompressor");
                return new GzipCompressor(input, output);
            }
            case null, default -> {
                logger.info("Invalid CompressionType `" + compressionType + "`, creating ZipCompressor instead");
                return new ZipCompressor(input, output);
            }
        }
    }

    public static Compressor getCompressor(CompressionType compressionType, Path input, Path output) {
        return getCompressor(compressionType, input.toFile(), output.toFile());
    }
}
