%
% Contains the current position and rotation of the robot through the
% specified environment.  Methods for obtaining vision data and
% rotating the robot are used to work with the fuzzy logic controller.
%
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
        
        %
        % Check to see if the coordinates are in the bounds of the
        % environment.
        %
        % Params:
        %     x - x-coordinate to check
        %     y - y-coordinate to check
        % Returns:
        %     bounded - true if the coordinates are in bound
        %
        function bounded = in_bounds(this, x, y)
            dim = size(this.m_env);
            bounded = x >= 1 && y >= 1 && x <= dim(1) && y <= dim(2);
        end
        
        %
        % Rotate the robot's direction by the specified angle.
        % 
        % Params:
        %     theta - angle to rotate robot by
        % Returns:
        %     angle - the angle after the rotation, in radians 
        %
        function angle = rotate(this, theta)
            this.m_pos(3) = mod(this.m_pos(3) + theta, Robot.TWO_PI);
            angle = this.m_pos(3);
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