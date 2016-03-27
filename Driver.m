function Driver()

    ENV_SIZE = 100;
    
    % Generate sample environment for testing
    function sample_env = gen_sample_env()
        sample_env = zeros(ENV_SIZE, ENV_SIZE);
        for r = 1:ENV_SIZE
            for c = 1:ENV_SIZE
                sample_env(r,c) = round(rand);
            end
        end
    end

    robot = Robot(gen_sample_env());
    
    %while true
        
        % Look around, obtain vision data
        vision_data = robot.scan();
        
        % Pass vision data to fuzzy controller
        
        % Use fuzzy output to decide movement
        
    %end
end