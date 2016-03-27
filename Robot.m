classdef Robot
    properties
        m_env; % Environment
        m_pos; % Current position and rotation
    end
    methods
        
        %
        % Create new robot to move around in the environment.
        % 
        % Params:
        %     env - environment to move in
        %
        function this = Robot(env)
            this.m_env = env;
            this.m_pos = [0,0,0];
        end
        
        %
        % Scan the environment and return the vision data.
        %
        % Returns:
        %     vision_data - array of detected obstacles
        %      
        function vision_data = scan(this)
            vision_data = 0;
        end
        
    end
end