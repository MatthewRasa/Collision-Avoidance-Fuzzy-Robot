classdef Robot
    properties(Constant)
        
        %
        % SCAN_RADIUS - Radius of the robot's vision
        % SCAN_FOV - Field of view angle for each side
        % SCAN_CIR - Block circumference of the scan field 
        % SCAN_INC - Angle to increment by when scanning
        % TWO_PI - Constant for 2 * pi
        %
        SCAN_RADIUS = 10;
        SCAN_FOV = pi / 4;
        SCAN_CIR = 2 * round(Robot.SCAN_RADIUS * Robot.SCAN_FOV)
        SCAN_INC = 2 * Robot.SCAN_FOV / Robot.SCAN_CIR;
        TWO_PI = 2 * pi;
        
    end
    properties(SetAccess = private)
        m_env; % Environment
        m_pos; % Current position and rotation
    end
    methods
        
        %
        % Create new robot to move around in the environment.
        % 
        % Params:
        %     env - environment to move in
        % Returns:
        %     this - a new Robot object
        %
        function this = Robot(env)
            this.m_env = env;
            this.m_pos = [1;1;0];
        end
        
        function r = dist(this, x0, y0, x1, y1)
            r = sqrt((x1 - x0)^2 + (y1 - y0)^2);
        end
        
        function bounded = in_bounds(this, x, y)
            dim = size(this.m_env);
            bounded = x >= 1 && y >= 1 && x <= dim(1) && y <= dim(2);
        end
        
        %
        % Scan the environment and return the vision data.
        %
        % Returns:
        %     vision_data - array of detected obstacles
        %      
        function vision_data = scan(this)
            vision_data = zeros(Robot.SCAN_CIR, 1);
            theta = this.m_pos(3) - Robot.SCAN_FOV;
            for i = 1:Robot.SCAN_CIR
                for r = 1:Robot.SCAN_RADIUS
                    row = this.m_pos(2) + round(r * sin(theta));
                    col = this.m_pos(1) + round(r * cos(theta));
                    if ~this.in_bounds(col, row) || this.m_env(row, col)
                        vision_data(i) = r;
                        break;
                    end
                end
                theta = theta + Robot.SCAN_INC;
            end
        end
        
    end
end