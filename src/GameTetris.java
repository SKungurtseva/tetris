import javax.swing.*; //библиотека для графических элементов
import java.awt.*; //библиотека для внешних форм
import java.awt.event.*; //библиотека для обработки событий клавиш
import java.io.*;
import java.util.*; //библиотека рандомных событий


public class GameTetris {
    final String TITLE_OF_PROGRAM = "Tetris";
    final int BLOCK_SIZE = 25; //в пикселях
    final int ARC_RADIUS = 6; //закругление краев падающих фигур
    final int FIELD_WIDTH = 10; //ширина поля in block
    final int FIELD_HEIGHT = 18; //высота поля in block
    final int START_LOCATION = 180; //определяет начальное положение левого верхнего угла окна игры
    final int FIELD_DX = 15; //для начального окна
    final int FIELD_DY = 100;
    final int LEFT = 37; //коды клавиш
    final int RIGHT = 39;
    final int DOWN = 40;
    final int UP = 38;
    final int SHOW_DELAY = 200; //задержка анимации

    final int[][][] SHAPES = {
            {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {4, 0x00f0f0}}, //I длинная палка
            {{0, 0, 0, 0}, {0, 1, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {4, 0xf0f000}}, //O квадратик
            {{1, 0, 0, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x0000f0}}, //J
            {{0, 0, 1, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf0a000}}, //L
            {{0, 1, 1, 0}, {1, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x00f000}}, //S
            {{1, 1, 1, 0}, {0, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xa000f0}}, //T
            {{1, 1, 0, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf00000}}  //Z
    }; //трехмерный массив заготовок фигур. двоичная заготовка фигуры. каждая палка имеет свой буквенный эквивалент
    //этот массив содержит внутренний размер фигурки, и цвет

    final int[] SCORES = {100, 300, 700, 1500}; //массив очков чем больше исчезает строк в низу тем больше очков
    int gameScore = 0;//переменная хранит очки
    public int[][] mine = new int[FIELD_HEIGHT + 1][FIELD_WIDTH]; //для шахты он соответствует константам
    JFrame frame; //переменная обьекта основного окна
    JPanel panel = new JPanel(); //панель является областью описания игры
    JLabel label = new JLabel("score : 0"); //метка хранит счет, инициализированный 0 баллами
    Records records = new Records();
    JLabel label1 = new JLabel("highScore : " ); //метка хранит максимальный счет
    JLabel label2 = new JLabel(" ← : left        → : right");
    JLabel label3 = new JLabel("  ↓ : down      ↑ : rotate");

    Canvas canvasPanel = new Canvas(); //!!!панель на которой все будем рисовать
    Random random = new Random(); //генератор случайных чисел
    Figure figure = new Figure(); //обьявляем и создаем фигуры

    boolean gameOver = false; //определяет игра закончена
    final int[][] GAME_OVER_MSG = {
            {0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0}, //обеспечивает красивый ресунок
            {1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1},
            {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0},
            {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0},
            {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0},
            {1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0}};

    public static void main(String[] args) {

        new GameTetris().go(); //создаем обьект и вызываем его метод go
    }

    public void go() {
        frame = new JFrame(TITLE_OF_PROGRAM); //создаем окошко
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //закрытие окна нажимая на крестик в углу с права
        frame.setSize(FIELD_WIDTH * BLOCK_SIZE + FIELD_DX, FIELD_HEIGHT * BLOCK_SIZE + FIELD_DY); //ширину умножить на размер блока в пикселях
        frame.setLocation(START_LOCATION, START_LOCATION); //определяем стартовую позицию окна
        frame.setResizable(false); //окно нельзя менять в размерах
        canvasPanel.setBackground(Color.black); //цвет поля

        frame.addKeyListener(new KeyAdapter() { //добавляем событие прослушивания клавиатуры
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    if (e.getKeyCode() == DOWN) figure.drop();
                    if (e.getKeyCode() == UP) figure.rotate();
                    if (e.getKeyCode() == LEFT || e.getKeyCode() == RIGHT) figure.move(e.getKeyCode());
                }
                canvasPanel.repaint();
            }
        });
        //frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);

        panel.setLayout(new GridLayout(4, 1)); //инициализируем панель описания игры
        panel.add(label);
        label1.setForeground(Color.RED); //установим для содержимого метки красный шрифт
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);

        frame.setLayout(new BorderLayout()); //добавляем в форму основную панель и панель описания
        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        //frame.add(center, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.AFTER_LAST_LINE);

        frame.setVisible(true); //делаем окно видемым

        Arrays.fill(mine[FIELD_HEIGHT], 1);//иницеализируем дно стакана

        while (!gameOver) { //main loop of game - главный цикл игры ;)
            try {
                Thread.sleep(SHOW_DELAY); //задержка перед прорисовкой(через обработчик прерываний)
            } catch (Exception e) {
                e.printStackTrace();
            }
            canvasPanel.repaint(); //наше окно перерисовывается
            if (figure.isTouchGround()) { //проверяем коснулась ли фигура земли
                figure.leaveOnTheGround(); //оставляем на земле
                checkFilling(); //проверяем наполняемость
                figure = new Figure(); //старая осталась "валяться" создаем новую фигуру
                gameOver = figure.isCrossGround(); //проверяем не закончена ли наша игра, не пересеклась ли фигура с землей
            } else {
                figure.stepDown(); //иначе фигура падает вниз
            }
        }
    }

    public void checkFilling() {  //проверка заполнения линий для оседания
        int row = FIELD_HEIGHT - 1;
        int countFillRows = 0;
        while (row > 0) {
            int filled = 1;
            for (int col = 0; col < FIELD_WIDTH; col++)
                filled *= Integer.signum(mine[row][col]);
            if (filled > 0) {
                countFillRows++;
                for (int i = row; i > 0; i--) System.arraycopy(mine[i - 1], 0, mine[i], 0, FIELD_WIDTH);
            } else {
                row--;
            }
        }
        if (countFillRows > 0) {
            gameScore += SCORES[countFillRows - 1];
            //frame.setTitle(TITLE_OF_PROGRAM + " : " + gameScore);
            label.setText("score : " + gameScore); //показать счет
        }
    }

    public class Figure {
        private ArrayList<Block> figure = new ArrayList<Block>(); //массив обьектов
        private int[][] shape = new int[4][4]; //массив для вращения фигуры
        private int type, size, color; //тип фигуры, размер матрицы в которой находиться фигура, цвет
        private int x = random.nextInt(7); //стартовые рандомные координаты
        private int y = 0;

        Figure() {
            type = random.nextInt(SHAPES.length);
            size = SHAPES[type][4][0]; //размеры взятые из массива
            color = SHAPES[type][4][1]; //цвет
            if (size == 4) y = -1; //проверка

            for (int i = 0; i < size; i++) //цикл заполняет массив shape кусочком из общей константы SHAPES
                System.arraycopy(SHAPES[type][i], 0, shape[i], 0, SHAPES[type][i].length);
            createFromShape(); //создать из формы
        }

        public void createFromShape() {
            for (int x = 0; x < size; x++) //двойной цикл проходит по массиву shape, на основании этого массива создает фигуру
                for (int y = 0; y < size; y++)
                    if (shape[y][x] == 1) figure.add(new Block(x + this.x, y + this.y));
        }

        public void stepDown() { //фигура падает вниз
            for (Block block : figure)
                block.setY(block.getY() + 1); //для каждого элемента в этом блоке устанавливаем новую координату Y
            y++;
        }

        boolean isCrossGround() { //проверяет не встретилась ли "небо" с "землей"
            for (Block block : figure)
                if (mine[block.getY()][block.getX()] > 0) return true; //проверяется условие и координаты
            return false;
        }

        public void leaveOnTheGround() {
            for (Block block : figure)
                mine[block.getY()][block.getX()] = color; //присваиваем массиву нашей шахты цвет фигуры
        }

        boolean isTouchGround() {
            for (Block block : figure)
                if (mine[block.getY() + 1][block.getX()] > 0) return true; //проверяется условие и координаты
            return false;
        }

        boolean isTouchWall(int direction) { //не коснулась ли фигура стены
            for (Block block : figure) {
                if (direction == LEFT && (block.getX() == 0 || mine[block.getY()][block.getX() - 1] > 0)) return true;
                if (direction == RIGHT && (block.getX() == FIELD_WIDTH - 1 || mine[block.getY()][block.getX() + 1] > 0))
                    return true;
            }
            return false;
        }

        public void move(int direction) { //метод позволяет двигать фигуру во время падения
            if (!isTouchWall(direction)) {
                int dx = direction - 38;
                for (Block block : figure) block.setX(block.getX() + dx);
                x += dx;
            }
        }

        public void drop() { //позволяет сбрасывать фигуры вниз

            while (!isTouchGround()) stepDown();
        }

        boolean isWrongPosition() { //метод вращения позволяет вращать фигуру нажатием стрелки вверх
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                    if (shape[y][x] == 1) {
                        if (y + this.y < 0) return true;
                        if (x + this.x < 0 || x + this.x > FIELD_WIDTH - 1) return true;
                        if (mine[y + this.y][x + this.x] > 0) return true;
                    }
            return false;
        }

        public void rotate() {
            for (int i = 0; i < size / 2; i++)
                for (int j = i; j < size - 1 - i; j++) {
                    int tmp = shape[size - 1 - j][i];
                    shape[size - 1 - j][i] = shape[size - 1 - i][size - 1 - j];
                    shape[size - 1 - i][size - 1 - j] = shape[j][size - 1 - i];
                    shape[j][size - 1 - i] = shape[i][j];
                    shape[i][j] = tmp;
                }
            if (!isWrongPosition()) { //проверяет не вылезла ли наша вращающая фигура за пределы
                figure.clear();
                createFromShape();
            }
        }

        public void paint(Graphics g) {
            for (Block block : figure) block.paint(g, color); // проходим по массиву блоков в фигуре, при этом каждый обьект входящий в массив фигуры поподает в переменную block.paint и вызываем метод рисования для каждого блочка
        }
    }

    public class Block { //обеспечит работу с минимальной строительной единицей любой фигуры
        private int x, y; // блок имеет свои координаты

        public Block(int x, int y) {
            setX(x);
            setY(y);
        }

        public void setX(int x) {

            this.x = x;
        }

        public void setY(int y) {

            this.y = y;
        }

        public int getX() {

            return x;
        }

        public int getY() {

            return y;
        }

        public void paint(Graphics g, int color) { //метод который рисует
            g.setColor(new Color(color)); //устанавливаем цвет
            g.drawRoundRect(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, BLOCK_SIZE - 2, BLOCK_SIZE - 2, ARC_RADIUS, ARC_RADIUS); //рисуем прямоугольник
        }
    }

    public class Canvas extends JPanel { //обеспечивает процедуру рисования, в нем пишем код который будет рисовать (будем перекрывать метод  paint)

        @Override
        public void paint(Graphics g) {
            super.paint(g); //вызывает родительский метод paint что бы прорисовало
            for (int x = 0; x < FIELD_WIDTH; x++)
                for (int y = 0; y < FIELD_HEIGHT; y++)
                    if (mine[y][x] > 0) { //прорисовка фигур лежащих на земле
                        g.setColor(new Color(mine[y][x])); //цвет соответствует числу которое записано
                        g.fill3DRect(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, BLOCK_SIZE - 1, BLOCK_SIZE - 1, true);//благодоря mine массиву мы узнаем на сколько заполнена шахта
                    }
            if (gameOver) {
                records.writeToFail();
                records.loadHighScore();
                g.setColor(Color.white);
                for (int y = 0; y < GAME_OVER_MSG.length; y++)
                    for (int x = 0; x < GAME_OVER_MSG[y].length; x++)
                        if (GAME_OVER_MSG[y][x] == 1) g.fill3DRect(x * 11 + 18, y * 11 + 160, 10, 10, true);
            } else {
                figure.paint(g); //добовляем рисование фигуры
            }
        }
    }

    public class Records {
        File myFile = new File("score.txt");
        private int highScore;

        private void loadHighScore() {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(myFile));
                String line = reader.readLine();
                String[] numbersStr = line.split(" ");
                int[] numbers = new int[numbersStr.length];

                int counter = 0;
                for (String number : numbersStr) {
                    numbers[counter++] = Integer.parseInt(number);
                }

                for (int i = 0; i < numbers.length; i++) {
                    if (numbers[i] > highScore && highScore == 0) {
                        highScore = numbers[i];
                    }
                    if (numbers[i] > highScore) {
                        highScore = numbers[i];
                    }
                }

                //System.out.println(Arrays.toString(numbers));
                reader.close();
                label1.setText("highScore : " + highScore); //показать highScore
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        private void writeToFail() {
            try {
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(myFile, true)));
                writer.print(Integer.toString(gameScore));
                writer.print(" ");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
