#define CL_USE_DEPRECATED_OPENCL_2_0_APIS

#include <array>
#include <iostream>
#include <fstream>
#include <CL/cl.hpp>
#include <string>

using namespace std;

int main() {
	// Get all platforms (drivers)
	vector<cl::Platform> all_platforms;
	cl::Platform::get(&all_platforms);
	if (all_platforms.size() == 0) {
		cout << "No platforms found." << endl;
		exit(1);
	}
	cl::Platform default_platform = all_platforms[0];
	cout << "Using platform: " << default_platform.getInfo<CL_PLATFORM_NAME>() << endl;

	// Get default device of the default platform
	vector<cl::Device> all_devices;
	default_platform.getDevices(CL_DEVICE_TYPE_ALL, &all_devices);
	if (all_devices.size() == 0) {
		cout << "No devices found, check OpenCL installation!" << endl;
		exit(1);
	}
	cl::Device default_device = all_devices[0];
	cout << "Using device: " << default_device.getInfo<CL_DEVICE_NAME>() << endl;

	// Context links device and platform.
	cl::Context context({ default_device });
	cl::Program::Sources sources;



	// Metal Alloy Stuff:
	cout << "Enter metal alloy's height: ";
	int height;
	cin >> height;
	cout << "\nEnter the desired iterations: ";
	int iterations;
	cin >> iterations;

	int width = height * 2;
	int size = width * height;
	const int edge[] = { width, height };
	const double ST[] = { 100, 100 };
	const double C[] = { 0.75, 1.0, 1.25 };
	// Pseudo 2 dimensional array.
	double* temp = new double[size];
	// 33% max variation of percentages, that's about 8% variation each or 4% up and down.
	int* percent_1 = new int[size];
	int* percent_2 = new int[size];
	int* percent_3 = new int[size];
	for (int i = 0; i < size; i++) {
		temp[i] = 20;
		percent_1[i] = rand() % 8 + 29;
		percent_2[i] = rand() % 8 + 29;
		percent_3[i] = 100 - percent_1[i] - percent_2[i];
	}

	
	// Actual source of program(kernel) is here.
	string kernel_code =
		"	void kernel heat (global const double* TEMPERATURE									"
		"							, global const int* PERCENT_1								"
		"							, global const int* PERCENT_2								"
		"							, global const int* PERCENT_3								"
		"							, global const double* C									"
		"							, global const double* ST									"
		"							, global const int* EDGE									"
		"							, global double* newTemperature) {							"
		"		long int index = get_global_id(0);												"
		"		if (index == 0) {																"
		"			newTemperature[index] = ST[0];												"
		"		}																				"
		"		else if (index == EDGE[0] * EDGE[1] - 1) {										"
		"			newTemperature[index] = ST[1];												"
		"		}																				"
		"		else {																			"
		"			double outer_Sigma[] = { 0, 0, 0 };											"
		"			double inner_Sigma[] = { 0, 0, 0 };											"
		"			int neighbors = 0;															"
		"			int up = index - EDGE[0];													"
		"			int down = index + EDGE[0];													"
		"			int left = index - 1;														"
		"			int right = index + 1;														"
		"																						"
		"			if (up >= 0) {																"
		"				neighbors++;															"
		"				inner_Sigma[0] += TEMPERATURE[up] * PERCENT_1[up];						"
		"				inner_Sigma[1] += TEMPERATURE[up] * PERCENT_2[up];						"
		"				inner_Sigma[2] += TEMPERATURE[up] * PERCENT_3[up];						"
		"			}																			"
		"			if (down < EDGE[0] * EDGE[1]) {												"
		"				neighbors++;															"
		"				inner_Sigma[0] += TEMPERATURE[down] * PERCENT_1[down];					"
		"				inner_Sigma[1] += TEMPERATURE[down] * PERCENT_2[down];					"
		"				inner_Sigma[2] += TEMPERATURE[down] * PERCENT_3[down];					"
		"			}																			"
		"			if (left >= 0) {															"
		"				neighbors++;															"
		"				inner_Sigma[0] += TEMPERATURE[left] * PERCENT_1[left];					"
		"				inner_Sigma[1] += TEMPERATURE[left] * PERCENT_2[left];					"
		"				inner_Sigma[2] += TEMPERATURE[left] * PERCENT_3[left];					"
		"			}																			"
		"			if (right < EDGE[0] * EDGE[1]) {											"
		"				neighbors++;															"
		"				inner_Sigma[0] += TEMPERATURE[right] * PERCENT_1[right];				"
		"				inner_Sigma[1] += TEMPERATURE[right] * PERCENT_2[right];				"
		"				inner_Sigma[2] += TEMPERATURE[right] * PERCENT_3[right];				"
		"			}																			"
		"																						"
		"			outer_Sigma[0] = C[0] * inner_Sigma[0] / 100 / neighbors;					"
		"			outer_Sigma[1] = C[1] * inner_Sigma[1] / 100 / neighbors;					"
		"			outer_Sigma[2] = C[2] * inner_Sigma[2] / 100 / neighbors;					"
		"																						"
		"			newTemperature[index] = outer_Sigma[0] + outer_Sigma[1] + outer_Sigma[2];	"
		"		}																				"
		"	}																					";

	// Build kernel source.
	sources.push_back({ kernel_code.c_str(), kernel_code.length() });
	cl::Program program(context, sources);
	if (program.build({ default_device }) != CL_SUCCESS) {
		cout << "Error building: " << program.getBuildInfo<CL_PROGRAM_BUILD_LOG>(default_device) << endl;
		exit(1);
	}

	// Allocate space on the device.
	cl::Buffer buffer_Temperature(context, CL_MEM_READ_WRITE, sizeof(double) * size);
	cl::Buffer buffer_Percent_1(context, CL_MEM_READ_WRITE, sizeof(int) * size);
	cl::Buffer buffer_Percent_2(context, CL_MEM_READ_WRITE, sizeof(int) * size);
	cl::Buffer buffer_Percent_3(context, CL_MEM_READ_WRITE, sizeof(int) * size);
	cl::Buffer buffer_C(context, CL_MEM_READ_WRITE, sizeof(double) * 3);
	cl::Buffer buffer_ST(context, CL_MEM_READ_WRITE, sizeof(double) * 2);
	cl::Buffer buffer_EDGE(context, CL_MEM_READ_WRITE, sizeof(int) * 2);
	cl::Buffer buffer_newT(context, CL_MEM_READ_WRITE, sizeof(double) * size);

	// Create queue to which the commands will be pushed to the device.
	cl::CommandQueue queue(context, default_device);

	// Copy array to device
	queue.enqueueWriteBuffer(buffer_Percent_1, CL_TRUE, 0, sizeof(int) * size, percent_1);
	queue.enqueueWriteBuffer(buffer_Percent_2, CL_TRUE, 0, sizeof(int) * size, percent_2);
	queue.enqueueWriteBuffer(buffer_Percent_3, CL_TRUE, 0, sizeof(int) * size, percent_3);
	queue.enqueueWriteBuffer(buffer_C, CL_TRUE, 0, sizeof(double) * 3, C);
	queue.enqueueWriteBuffer(buffer_ST, CL_TRUE, 0, sizeof(double) * 2, ST);
	queue.enqueueWriteBuffer(buffer_EDGE, CL_TRUE, 0, sizeof(int) * 2, edge);

	for (int i = 0; i < iterations; i++) {
		// Send in the new data.
		queue.enqueueWriteBuffer(buffer_Temperature, CL_TRUE, 0, sizeof(double) * size, temp);


		// Run the kernel.
		cl::Kernel heat(program, "heat");
		heat.setArg(0, buffer_Temperature);
		heat.setArg(1, buffer_Percent_1);
		heat.setArg(2, buffer_Percent_2);
		heat.setArg(3, buffer_Percent_3);
		heat.setArg(4, buffer_C);
		heat.setArg(5, buffer_ST);
		heat.setArg(6, buffer_EDGE);
		heat.setArg(7, buffer_newT);
		queue.enqueueNDRangeKernel(heat, cl::NullRange, cl::NDRange(size), cl::NullRange);
		queue.finish();

		// Transfer data from the device to this program (host).
		double* newTemp = new double[size];
		// Read result C from the device to array C.
		queue.enqueueReadBuffer(buffer_newT, CL_TRUE, 0, sizeof(double) * size, newTemp);

		// The result will be sued in the next iteration.
		delete[] temp;
		temp = newTemp;
	}


	cout << "result:" << endl;
	for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
			cout << (int)temp[y * width + x] << " ";
		}
		cout << endl;
	}



	return 0;
}
