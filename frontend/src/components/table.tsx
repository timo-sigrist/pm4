import { getFormattedDate } from "@/utils/date"
import Loading from "./loading"

const Header = ({ columns, actions }: Readonly<{
  columns: Array<{ header: string, title?: string }>
  actions?: Array<{ icon: any, label?: string, onClick: (id: number) => void, hide?: (id: number) => boolean }>
}>) => {
  return (
    <thead className="bg-slate-200 w-full">
      <tr>
        {columns && columns.map((column, index) => {
          return (
            <th key={index} className="py-2 px-6 text-left text-sm">{column.header}</th>
          )
        })}
        {actions && actions.length > 0 && (
          <th className="py-2 px-6 text-right text-sm">Aktionen</th>
        )}
      </tr>
    </thead>
  )
}

const Item = ({ itemIndex, item, columns, actions }: Readonly<{
  itemIndex: number,
  item: any,
  columns: Array<{ header: string, title?: string, titleFunction?: ((value: any) => React.ReactNode) | undefined }>
  actions?: Array<{ icon: any, label?: string, onClick: (id: number) => void, hide?: (id: number) => boolean }>
}>) => {
  return (
    <tr className="bg-white border-t-4 border-slate-100">
      {columns && columns.map((column, index) => {
        return (
          <td key={index} className="py-4 px-6 text-left text-sm">
            {column.title !== undefined ?
              column.title === "date" ? getFormattedDate(item[column.title]) : item[column.title]
              : column.titleFunction ? column.titleFunction(item) : ''}
          </td>
        )
      })}
      {actions && actions.length > 0 && (
        <td className="text-right text-md pr-2 min-w-52">
          {actions && actions.map((action, index) => {
            return (
              <button key={index} onClick={() => action.onClick(itemIndex)} className={`rounded-md hover:bg-slate-100 text-sm mr-2 px-2 py-1.5 focus:outline-2 focus:outline-black duration-200 ${!action.label && "w-9"}`} hidden={action.hide ? action.hide(itemIndex) : false}>
                {action.icon && <action.icon className="w-5 h-5 mr-2" />}
                {action.label}
              </button>
            )
          })}
        </td>
      )}
    </tr>
  )
}

export default function Table({ className, data, columns, actions, loading, customBottom }: Readonly<{
  className?: string,
  data: Array<any>,
  columns: Array<{ header: string, title?: string, titleFunction?: ((value: any) => React.ReactNode) | undefined }>
  actions?: Array<{ icon: any, label?: string, onClick: (id: number) => void, hide?: (id: number) => boolean }>
  loading?: boolean
  customBottom?: React.ReactNode
}>) {
  return (
    <div className="overflow-auto">
      <table className={`table-auto w-full rounded-lg overflow-hidden ${className}`}>
        <Header
          columns={columns}
          actions={actions}
        ></Header>
        <tbody>
          {loading && (
            <>
              <tr>
                <td
                  colSpan={columns.length + 1}
                  className="border-t-4 border-slate-100 bg-white h-20"
                >
                  <Loading />
                </td>
              </tr>
            </>
          )}
          {!loading && data && data.length === 0 && (
            <>
              <tr>
                <td
                  colSpan={columns.length + 1}
                  className="border-t-4 border-slate-100 bg-white h-20 text-center text-sm"
                >
                  Keine Daten erfasst
                </td>
              </tr>
            </>
          )}
          {!loading && data && data.map((item, index) => {
            return (<Item key={index} itemIndex={index} item={item} columns={columns} actions={actions}></Item>)
          })}
          {customBottom}
        </tbody>
      </table>
    </div>
  );
}