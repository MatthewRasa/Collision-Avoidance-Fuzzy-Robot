function Driver()

    ENV_DIM = [16 32]; % Rows, columns
    UPDATE_RATE = 1.0 / 10.0; % Interval the robot scans
    
    m_env = [];
    m_gui = [];
    m_robot = [];
    
    % Initialize the environment, GUI, and robot
    function init()
        javaaddpath('./gui.jar');
        m_env = gen_sample_env();
        m_gui = gui.Grid(m_env);
        m_robot = Robot(m_env, m_gui);
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
        for r = 1:ENV_DIM(1)
            for c = 1:ENV_DIM(2)
                sample_env(r,c) = round(rand);
            end
        end
        sample_env(1,1) = 0;
    end

    init();
    run();
end