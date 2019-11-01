///*|-----------------------------------------------------------------------------
// *|            This source code is provided under the Apache 2.0 license      --
// *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
// *|                See the project's LICENSE.md for details.                  --
// *|           Copyright Thomson Reuters 2015. All rights reserved.            --
///*|-----------------------------------------------------------------------------

#include "Consumer.h"

using namespace thomsonreuters::ema::access;
using namespace thomsonreuters::ema::rdm;
using namespace std;

void AppClient::onRefreshMsg( const RefreshMsg& refreshMsg, const OmmConsumerEvent& )
{
	cout << endl << "Item Name: " << ( refreshMsg.hasName() ? refreshMsg.getName() : EmaString( "<not set>" ) ) << endl
		<< "Service Name: " << (refreshMsg.hasServiceName() ? refreshMsg.getServiceName() : EmaString( "<not set>" ) );

	cout << endl << "Item State: " << refreshMsg.getState().toString() << endl;

	if ( DataType::FieldListEnum == refreshMsg.getPayload().getDataType() )
		decode( refreshMsg.getPayload().getFieldList() );
}

void AppClient::onUpdateMsg( const UpdateMsg& updateMsg, const OmmConsumerEvent& )
{
	cout << endl << "Item Name: " << ( updateMsg.hasName() ? updateMsg.getName() : EmaString( "<not set>" ) ) << endl
		<< "Service Name: " << (updateMsg.hasServiceName() ? updateMsg.getServiceName() : EmaString( "<not set>" ) ) << endl;

	if ( DataType::FieldListEnum == updateMsg.getPayload().getDataType() )
		decode( updateMsg.getPayload().getFieldList() );
}

void AppClient::onStatusMsg( const StatusMsg& statusMsg, const OmmConsumerEvent& )
{
	cout << endl << "Item Name: " << ( statusMsg.hasName() ? statusMsg.getName() : EmaString( "<not set>" ) ) << endl
		<< "Service Name: " << (statusMsg.hasServiceName() ? statusMsg.getServiceName() : EmaString( "<not set>" ) );

	if ( statusMsg.hasState() )
		cout << endl << "Item State: " << statusMsg.getState().toString() << endl;
}

void AppErrorClient::onInvalidHandle( UInt64 handle, const EmaString& text )
{
	cout << endl << "onInvalidHandle callback function" << endl;
	cout << "Invalid handle: " << handle << endl;
	cout << "Error text: " << text << endl;
}

void AppErrorClient::onInaccessibleLogFile( const EmaString& fileName, const EmaString& text )
{
	cout << endl << "onInaccessibleLogFile callback function" << endl;
	cout << "Inaccessible file name: " << fileName <<endl;
	cout << "Error text: " << text << endl;
}

void AppErrorClient::onSystemError( Int64 code, void* address, const EmaString& text)
{
	cout << endl << "onSystemError callback function" << endl;
	cout << "System Error code: " << code << endl;
	cout << "System Error Address: " << address << endl;
	cout << "Error text: " << text << endl;
}
	
void AppErrorClient::onMemoryExhaustion( const EmaString& text )
{
	cout << endl << "onMemoryExhaustion callback function" << endl;
	cout << "Error text: " << text << endl;
}
	
void AppErrorClient::onInvalidUsage( const EmaString& text, Int32 errorCode )
{
	cout << "onInvalidUsage callback function" << endl;
	cout << "Error text: " << text << endl;
	cout << "Error code: " << errorCode << endl;
}

void AppClient::decode( const FieldList& fl )
{
	while ( fl.forth() )
	{
		const FieldEntry& fe = fl.getEntry();

		cout << "Name: " << fe.getName() << " Value: ";

		if ( fe.getCode() == Data::BlankEnum )
			cout << " blank" << endl;
		else
			switch ( fe.getLoadType() )
		{
			case DataType::RealEnum:
				cout << fe.getReal().getAsDouble() << endl;
				break;
			case DataType::DateEnum:
				cout << (UInt64)fe.getDate().getDay() << " / " << (UInt64)fe.getDate().getMonth() << " / " << (UInt64)fe.getDate().getYear() << endl;
				break;
			case DataType::TimeEnum:
				cout << (UInt64)fe.getTime().getHour() << ":" << (UInt64)fe.getTime().getMinute() << ":" << (UInt64)fe.getTime().getSecond() << ":" << (UInt64)fe.getTime().getMillisecond() << endl;
				break;
			case DataType::IntEnum:
				cout << fe.getInt() << endl;
				break;
			case DataType::UIntEnum:
				cout << fe.getUInt() << endl;
				break;
			case DataType::AsciiEnum:
				cout << fe.getAscii() << endl;
				break;
			case DataType::ErrorEnum:
				cout << fe.getError().getErrorCode() << "( " << fe.getError().getErrorCodeAsString() << " )" << endl;
				break;
			case DataType::EnumEnum:
				fe.hasEnumDisplay() ? cout << fe.getEnumDisplay() << endl : cout << fe.getEnum() << endl;
				break;
			case DataType::RmtesEnum:
				cout << fe.getRmtes().toString() << endl;
				break;
			default:
				cout << endl;
				break;
		}
	}
}

int main()
{
	AppClient client;
	AppErrorClient errorClient;
	UInt64 invalidHandle = 0;
	OmmConsumer consumer( OmmConsumerConfig().username( "user" ).operationModel( OmmConsumerConfig::UserDispatchEnum ), errorClient );
	consumer.reissue( ReqMsg(), invalidHandle );
	consumer.submit ( GenericMsg(), invalidHandle );
	consumer.submit ( PostMsg(), invalidHandle );
	consumer.registerClient( ReqMsg().name( "IBM.N" ).serviceName( "DIRECT_FEED" ), client );
	unsigned long long startTime = getCurrentTime();
	while ( startTime + 60000 > getCurrentTime() )
		consumer.dispatch( 10 );		// calls to onRefreshMsg(), onUpdateMsg(), or onStatusMsg() execute on this thread

	return 0;
}
