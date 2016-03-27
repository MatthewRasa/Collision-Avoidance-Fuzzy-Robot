function Driver()

    WINDOW_POSITION = [0 0 1400 700];
    REFRESH_RATE = 1.0 / 30.0;
    ENV_DIM = [32 16];
    BLANK_COLOR = [1, 1, 1];
    OBS_COLOR = [0.5, 0.5, 0.5];
    
    m_canvas = [];
    m_env = [];
    m_robot = [];
    
    % Initialize the environment, grid, and robot
    function init()
        m_canvas = figure('position', WINDOW_POSITION);
        m_env = gen_sample_env();
        m_robot = Robot(m_env);
        axes('position', [0 0 1 1]);
        set(gca, 'XAxisLocation', 'top', 'Ydir', 'reverse', 'XTick', 0:ENV_DIM(1), 'YTick', 0:ENV_DIM(2));
        gen_grid();
    end

    % Redraw the grid and move the robot based on the fuzzy logic
    function redraw()
        while ishandle(m_canvas)
            % Redraw grid
            refresh(m_canvas);
            
            % Look around, obtain vision data
            vision_data = m_robot.scan();

            % Pass vision data to fuzzy controller
            fuzzy_output = 0;

            % Use fuzzy output to decide movement
            m_robot.rotate(fuzzy_output);
            
            pause(REFRESH_RATE);
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

    % Generate the grid for the first time
    function gen_grid()
        for c = 1:ENV_DIM(1)
            for r = 1:ENV_DIM(2)
                if m_env(c, r)
                    clr = OBS_COLOR;
                else
                    clr = BLANK_COLOR;
                end
                rectangle('Position', [c - 1, r - 1, 1, 1], 'FaceColor', clr);
            end
        end
    end

    init();
    redraw();
end