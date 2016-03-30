function Driver()

    DEBUG = true;
    ENV_DIM = [16 32]; % Rows, columns
    UPDATE_RATE = 1.0 / 30.0; % Interval the robot scans
    
    m_env = [];
    m_gui = [];
    m_robot = [];
    
    % Initialize the environment, GUI, and robot
    function init()
        javaaddpath('./jars/gui.jar');
        javaaddpath('./jars/paths.jar');
        path_obj = com.kevinbohinski.CSC47002.PathGenerator(3, ENV_DIM(2), ENV_DIM(1), 9);
        m_env = rot90(path_obj.getPath(0));
        %m_env = gen_sample_env();
        m_gui = gui.Grid(m_env, DEBUG);
        m_robot = Robot(m_env, m_gui, readfis('FuzzyRobot.fis'));
    end

    % Redraw the grid and move the robot based on the fuzzy logic
    function run()
        while m_gui.isRunning()
            m_robot.update(); % Draw robot
            pause(UPDATE_RATE); % Sleep thread for the update rate
        end
    end
    
    % Generate sample environment for testing
    function sample_env = gen_sample_env()
        sample_env = zeros(ENV_DIM);
        for i=0:3
            sample_env(1:ENV_DIM(1), 4 * i + 1:4 * (i + 1) - 1) = 0;
            sample_env(1:ENV_DIM(1), 4 * (i + 1), 1) = 1;
        end
        sample_env(12:16, 4) = 0;
        sample_env(1:4, 8) = 0;
        sample_env(12:16, 12) = 0;
        sample_env(1:4, 16) = 0;
        
        sample_env(1:ENV_DIM(1), 1) = 1;
        sample_env(1:ENV_DIM(1), ENV_DIM(2)) = 1;
        sample_env(1, 1:ENV_DIM(2)) = 1;
        sample_env(ENV_DIM(1), 1:ENV_DIM(2)) = 1;
    end

    init();
    run();
end