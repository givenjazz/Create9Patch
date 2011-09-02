import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class Create9Patch {
    public static void main(String args[]) {
        int length = 5;
        int start = 5;

        if (args.length < 1) {
            System.out.println("usage: Create9Patch source...");
            return;
        }

        for (String fileName : args) {
            try {
                if ("*".equals(fileName)) {
                    create9patch(length, start, ".");
                } else {
                    create9patch(length, start, fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void create9patch(int length, int start, final String fileName)
            throws IOException {

//        System.out.println(fileName);
        final File srcFile = new File(fileName);

        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            for (File f : files) {
                create9patch(length, start, f.getAbsolutePath());
            }
        } else {
            File destFile = null;
            if (!srcFile.getName().endsWith(".9.png") && srcFile.getName().endsWith(".png")) {
                destFile = new File(srcFile.getName().substring(0, srcFile.getName().length() - 3)
                        + "9.png");
            } else {
                return;
            }

            BufferedImage source = ImageIO.read(srcFile);
            int width = source.getWidth();
            int height = source.getHeight();
            
            final int miniumSize = (start + length) * 2;
            if(width < miniumSize || height < miniumSize){
                System.out.println(destFile.getName() + " is too small.");
                return;
            }

            BufferedImage dest = new BufferedImage(width + 2, height + 2,
                    BufferedImage.TYPE_INT_ARGB);

            int[] rgbs = new int[width * height];
            source.getRGB(0, 0, width, height, rgbs, 0, width);
            dest.setRGB(1, 1, width, height, rgbs, 0, width);

            int[] black = new int[length];
            for (int i = 0; i < black.length; i++) {
                black[i] = 0xFF000000;
            }

            dest.setRGB(start - 1, 0, length, 1, black, 0, length);
            dest.setRGB(0, start - 1, 1, length, black, 0, 1);
            dest.setRGB(width - 2 - length, 0, length, 1, black, 0, length);
            dest.setRGB(0, height - 2 - length, 1, length, black, 0, 1);

            ImageIO.write(dest, "png", destFile);
            System.out.println("created : " + destFile.getName());
        }
    }
}
